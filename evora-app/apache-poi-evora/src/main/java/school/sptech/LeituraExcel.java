package school.sptech;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal que orquestra a leitura do arquivo Excel.
 * Pense nela como o "gerente do projeto de leitura".
 * Ela prepara tudo e delega o trabalho pesado para o SheetHandler.
 */
public class LeituraExcel {

    // Um logger para registrar informações sobre o que está acontecendo (início, fim, erros).
    private static final Logger logger = LoggerFactory.getLogger(LeituraExcel.class);

    // Dependência para salvar os dados no banco. Será "injetada" pelo construtor.
    private final InstituicaoDao instituicaoDao;

    /**
     * Construtor da classe. Ele não cria suas dependências, ele as recebe.
     * Isso é chamado de "Injeção de Dependência", uma ótima prática de programação.
     * @param instituicaoDao O objeto responsável por se comunicar com o banco de dados.
     */
    public LeituraExcel(InstituicaoDao instituicaoDao) {
        this.instituicaoDao = instituicaoDao;
    }

    /**
     * Método público que inicia todo o processo de leitura e processamento.
     * @param excelInputStream O fluxo de dados do arquivo .xlsx.
     * @param anoFiltro O ano que queremos filtrar.
     * @param ufFiltro A UF que queremos filtrar.
     *
     */
    public void processarPlanilha(InputStream excelInputStream, int anoFiltro, String ufFiltro) {
        logger.info("Iniciando leitura de arquivo Excel com filtro para Ano: {} e UF: {}", anoFiltro, ufFiltro);
        try {
            // Abre o "pacote" do arquivo Excel (xlsx é um zip de vários arquivos XML) de forma otimizada.
            OPCPackage pkg = OPCPackage.open(excelInputStream);

            // Cria o leitor de baixo nível do XSSF, que nos dá acesso aos componentes do arquivo sem carregar tudo.
            XSSFReader leitor = new XSSFReader(pkg);

            // Pega a Tabela de Strings Compartilhadas (o nosso "dicionário" de textos).
            // Isso é como o Exel funciona, se uma palavra em uma celula se repete em várias outras celulas
            // o exel add ela em um "dicionario" e sempre que ela aparece ele usa um valor para representar essa palavra
            // sst é esse dicionario que vamos consultar
            SharedStringsTable sst = (SharedStringsTable) leitor.getSharedStringsTable();

            // Cria um parser de XML (SAX). É ele quem vai ler o arquivo da planilha evento por evento.
            // Isso é tipo um HTML do exel, um jeito dele estrutar dados, como <row>Linha <c> Celula </c> Linha</row>
            XMLReader parser = XMLReaderFactory.createXMLReader();

            // AQUI A MÁGICA ACONTECE: Dizemos ao parser: "Quando você ler os eventos, avise a este cara aqui".
            // Nós passamos nosso SheetHandler customizado para ser o "ouvinte" dos eventos.
            parser.setContentHandler(new SheetHandler(sst, instituicaoDao, anoFiltro, ufFiltro));

            // Pega o fluxo de dados da primeira planilha encontrada.
            try (InputStream planinha = leitor.getSheetsData().next()) {
                InputSource pesquisaPlaninha = new InputSource(planinha);
                // Inicia o processo! O parser começa a ler o stream e a "gritar" eventos para o SheetHandler.
                // Ele lê o "HTML" do exel
                parser.parse(pesquisaPlaninha);
            }


        } catch (Exception e) {
            logger.error("Falha crítica durante o processamento do Excel.", e);
            
        }
    }

    /**
     * Esta é a classe mais importante. É o nosso "anotador inteligente".
     * Ela herda de DefaultHandler para já ter implementações padrão para todos os eventos SAX,
     * e nós só sobrescrevemos os métodos que nos interessam (startElement, endElement, characters).
     */
    private static class SheetHandler extends DefaultHandler {

        // Constantes para deixar o código legível, em vez de usar "números mágicos".
        // Da nome para as colunas e linhas, inves de usar os números puros
        private static final int COL_ANO = 0;
        private static final int COL_UF = 1;
        private static final int COL_ID_MUNICIPIO = 2;
        private static final int COL_ID_INSTITUICAO = 7;
        private static final int COL_NOME_INSTITUICAO = 8;
        private static final int LINHA_CABECALHO = 1;
        private static final int LOG_PROGRESSO_INTERVALO = 10000;

        // Referências para os objetos que o Handler precisa para trabalhar.
        private final SharedStringsTable sst; // O dicionário de textos.
        private final InstituicaoDao instituicaoDao; // O objeto para salvar no banco.
        private final int anoFiltro; // O ano a ser filtrado.
        private final String ufFiltro; // A UF a ser filtrada.

        // Variáveis de estado: guardam informações temporárias durante a leitura.
        private String ultimoConteudo; // Guarda o texto da célula que está sendo lida.
        private boolean proximoEhString; // Flag que avisa: "o conteúdo que está vindo é um ID do dicionário!".
        private final List<String> linhaAtual = new ArrayList<>(); // A nossa "folha de anotações" para a linha atual.
        private long contadorLinhas = 0; // Para saber em qual linha estamos (e pular o cabeçalho).
        private long contadorSalvos = 0; // Para estatísticas e logs.

        SheetHandler(SharedStringsTable sst, InstituicaoDao dao, int anoFiltro, String ufFiltro) {
            this.sst = sst;
            this.instituicaoDao = dao;
            this.anoFiltro = anoFiltro;
            this.ufFiltro = ufFiltro;
        }

        // Chamado toda vez que o parser encontra o início de uma tag XML (ex: <row>, <c>).
        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) {
            // Se a tag for "c" (célula)...
            if ("c".equals(name)) {
                // ...verificamos seu atributo "t". Se for "s", significa que o conteúdo é uma string do SST.
                String cellType = attributes.getValue("t");
                proximoEhString = "s".equals(cellType);
            }
            // Limpamos o conteúdo anterior para nos prepararmos para ler o conteúdo da nova tag.
            ultimoConteudo = "";
        }

        // Chamado quando o parser encontra o texto/conteúdo dentro de uma tag.
        @Override
        public void characters(char[] ch, int start, int length) {
            // Apenas acumulamos o conteúdo encontrado.
            ultimoConteudo += new String(ch, start, length);
        }

        // Chamado toda vez que o parser encontra o fim de uma tag XML (ex: </row>, </c>).
        @Override
        public void endElement(String uri, String localName, String name) {
            // Se a flag nos avisou que a célula era uma string do SST...
            if (proximoEhString) {
                // ...convertemos o conteúdo (que é um número, ex: "5") para inteiro.
                int idx = Integer.parseInt(ultimoConteudo);
                // ...usamos esse número para buscar o texto real no dicionário (SST).
                ultimoConteudo = new XSSFRichTextString(sst.getItemAt(idx).getString()).toString();
                // ...e desligamos a flag.
                proximoEhString = false;
            }

            // Se a tag que terminou for "v" (valor da célula)...
            if ("v".equals(name)) {
                // ...adicionamos o conteúdo final à nossa lista da linha atual.
                linhaAtual.add(ultimoConteudo);
            }

            // Se a tag que terminou for "row" (o fim de uma linha inteira)...
            if ("row".equals(name)) {
                contadorLinhas++;
                // Ignoramos a primeira linha, que geralmente é o cabeçalho.
                if (contadorLinhas > LINHA_CABECALHO) {
                    // Delegamos a lógica de negócio para um método separado e mais limpo.
                    processarLinha(linhaAtual);
                }

                // A cada X linhas, imprimimos um log para saber o progresso.
                if (contadorLinhas % LOG_PROGRESSO_INTERVALO == 0) {
                    logger.info("Processadas {} linhas. Salvas {} instituições até agora.", contadorLinhas, contadorSalvos);
                }

                // ESSENCIAL: Limpamos a lista para recomeçar a anotação para a próxima linha.
                linhaAtual.clear();
            }
        }

        /**
         * Método auxiliar que contém a lógica de negócio (o que fazer com os dados da linha).
         * @param linha Uma lista de strings com todos os valores da linha que acabou de ser lida.
         */
        private void processarLinha(List<String> linha) {
            // Verificação de segurança: a linha tem colunas suficientes para evitar erros?
            if (linha.size() <= COL_NOME_INSTITUICAO) {
                return; // Se não, simplesmente ignoramos esta linha.
            }

            try {
                // Extraímos os dados das colunas usando nossas constantes legíveis.
                double ano = Double.parseDouble(linha.get(COL_ANO)); // Excel trata números como double.
                String uf = linha.get(COL_UF);

                // Aplicamos nossa regra de negócio (o filtro).
                if (ano == anoFiltro && ufFiltro.equalsIgnoreCase(uf)) {
                    // Se a linha corresponde ao filtro, criamos o objeto Instituicao.
                    Instituicao instituicao = new Instituicao();
                    instituicao.setUf(uf.toUpperCase());
                    instituicao.setIdMunicipio((int) Double.parseDouble(linha.get(COL_ID_MUNICIPIO)));
                    instituicao.setIdInstituicao((int) Double.parseDouble(linha.get(COL_ID_INSTITUICAO)));
                    instituicao.setNome(linha.get(COL_NOME_INSTITUICAO).toUpperCase());

                    // Mandamos o DAO salvar o objeto no banco de dados.
                    instituicaoDao.save(instituicao);
                    contadorSalvos++;
                }
            } catch (NumberFormatException e) {
                // Se uma célula que deveria ser número tiver um texto, este erro acontece.
                // Nós registramos o aviso e continuamos para a próxima linha, sem quebrar a aplicação.
                logger.warn("Linha {} ignorada devido a erro de formatação de número: {}", contadorLinhas, e.getMessage());
            } catch (Exception e) {
                // Captura qualquer outro erro inesperado naquela linha.
                logger.error("Erro inesperado ao processar a linha {}. Dados da linha: {}", contadorLinhas, linha, e);
            }
        }
    }
}