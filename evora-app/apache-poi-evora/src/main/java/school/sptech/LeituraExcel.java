package school.sptech;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;

/**
 * Classe principal que orquestra a leitura do arquivo Excel.
 * Pense nela como o "gerente do projeto de leitura".
 * Ela prepara tudo e delega o trabalho pesado para o SheetHandler.
 */
public class LeituraExcel {

    // Um logger para registrar informações sobre o que está acontecendo (início, fim, erros).
    private static final Logger logger = LoggerFactory.getLogger(LeituraExcel.class);

    public LeituraExcel() {
    }

    public void processarPlanilhaInstituicao(InputStream excelInputStream, int anoFiltro, String ufFiltro, InstituicaoDao instituicaoDao) {
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
            parser.setContentHandler(new SheetHandlerInstituicao(sst, instituicaoDao, anoFiltro, ufFiltro));

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

    public void processarPlanilhaCurso(InputStream excelInputStream, int anoFiltro, String ufFiltro, CursosDao cursosDao) {
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
            parser.setContentHandler(new SheetHandlerCurso(sst, cursosDao, anoFiltro, ufFiltro));

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

}