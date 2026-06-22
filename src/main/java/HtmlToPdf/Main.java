package HtmlToPdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PdfAConformance;

import org.apache.pdfbox.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

import HtmlToPdf.AutoFont.CSSFont;

public class Main {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Needs 1 Argument: Path to HTML file.");
      return;
    }

    PdfAConformance pdfaLevel = PdfAConformance.PDFA_3_A;

    for (int i = 1; i < args.length; i++) {
      String arg = args[i].toLowerCase();

      // Both flags select the PDF 2.0 base. "pdf/ua-2" is kept as an alias of "pdf/a-4".
      if (arg.equals("pdf/a-4") || arg.equals("pdf/ua-2")) {
        pdfaLevel = PdfAConformance.PDFA_4_A;
      }
    }

    // The PDF/UA version is dictated by the base PDF version, not chosen independently:
    // PDF/A-4 is PDF 2.0 -> PDF/UA-2; PDF/A-3a is PDF 1.7 -> PDF/UA-1. The other pairings
    // (UA-1 on PDF 2.0, UA-2 on PDF 1.7) are not valid, so they are never produced.
    int pdfUaVersion = pdfaLevel == PdfAConformance.PDFA_4_A ? 2 : 1;

    File inFile = new File(args[0]);
    String in = inFile.getAbsolutePath();

    Path inPath = Paths.get(in);
    Path folderPath = inPath.getParent();
    String folderPathString = folderPath.toString();

    String out = folderPathString + "/output.pdf";

    System.out.println("Start");

    try {
      generate(in, out, pdfaLevel, pdfUaVersion);
    } catch (Exception e) {
      System.out.println("Error");
      e.printStackTrace();
    }

    System.out.println("Finish");
  }

  public static void generate(String in, String out, PdfAConformance pdfaLevel, int pdfUaVersion) throws Exception {
    try (FileOutputStream os = new FileOutputStream(out)) {
      Path inPath = Paths.get(in);

      W3CDom w3cDom = new W3CDom();
      org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(new File(in), "UTF-8");
      org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);

      Path folderPath = inPath.getParent();
      String folderPathString = folderPath.toString();

      Path fontDirectory = Paths.get(folderPathString + "/fonts");
      List<CSSFont> fonts = AutoFont.findFontsInDirectory(fontDirectory);

      PdfRendererBuilder builder = new PdfRendererBuilder();
      AutoFont.toBuilder(builder, fonts);

      builder.useFastMode();
      builder.usePdfUaAccessibility(true, pdfUaVersion);
      builder.usePdfAConformance(pdfaLevel);
      builder.withW3cDocument(w3cDoc, folderPathString);

      try (InputStream colorProfile = Main.class.getResourceAsStream("/colorspaces/sRGB.icc")) {
        byte[] colorProfileBytes = IOUtils.toByteArray(colorProfile);
        builder.useColorProfile(colorProfileBytes);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      builder.toStream(baos);
      builder.run();

      Files.write(Paths.get(out), baos.toByteArray());
    }
  }
}