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

    File inFile = new File(args[0]);
    String in = inFile.getAbsolutePath();

    Path inPath = Paths.get(in);
    Path folderPath = inPath.getParent();
    String folderPathString = folderPath.toString();

    String out = folderPathString + "/output.pdf";

    System.out.println("Start");

    try {
      generate(in, out);
    } catch (Exception e) {
      System.out.println("Error");
      e.printStackTrace();
    }

    System.out.println("Finish");
  }

  public static void generate(String in, String out) throws Exception {
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
      builder.usePdfUaAccessbility(true);
      builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_A);
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