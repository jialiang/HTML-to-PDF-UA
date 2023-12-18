# HTML-to-PDF-UA

Java App to generate PDF/UA and PDF/A-3A compliant PDFs from HTML using the [OpenHTMLtoPDF](https://github.com/danfickle/openhtmltopdf) library.

PDF/UA is a set of requirements for universally-accessible PDF documents. You can refer to the document [PDF/UA in a Nutshell](https://github.com/jialiang/HTML-to-PDF-UA/blob/master/PDFUA-in-a-Nutshell-PDFUA.pdf) published by the PDF association to learn more about PDF/UA.

## Example

In the [Example folder](https://github.com/jialiang/HTML-to-PDF-UA/tree/master/example), you can see an example input HTML and output PDF.

The output PDF has been checked using the following tools and found to be PDF/UA and PDF/A-3A compliant:

- [PDF Accessibility Check 3](https://pdfua.foundation/en/pdf-accessibility-checker-pac/) _(Accessibility report included in Example folder)_
- [Adobe Acrobat Preflight Tool](https://helpx.adobe.com/acrobat/using/create-verify-pdf-accessibility.html)
- [VeraPDF Conformance Checker](https://docs.verapdf.org/validation/)

## Download

You can download the compiled Java app from the [Releases page](https://github.com/jialiang/HTML-to-PDF-UA/releases/).

## Usage

- Make sure you have Java installed.
- In the same folder as the HTML file you want to convert, create a folder called "fonts". This folder must contain all the fonts used by the HTML in TrueType _(.ttf)_ format _(even the ones installed locally on your PC needs to be included)_. It uses the font name reported by the font file; It checks if the filename contains certain keywords to determine the font weight _(e.g. thin, extra-light, light, regular, medium, semi-bold, bold, extra-bold, black)_ and font style _(e.g. italic)_.
- Make sure to set the page size and margins of the generated PDF by including this style block in your HTML file:

  ```
  @page {
    margin: 0;
    size: a4;
  }
  ```

  See [this example](https://github.com/danfickle/openhtmltopdf/wiki/Page-features) for more options.

- Make sure to define these metadata in the `<head>` of your HTML file:

  ```
  <meta name="subject" content="the subject" />
  <meta name="author" content="the author" />
  <meta name="description" content="the description" />
  ```

- Remember to define the bookmarks in the `<head>` of your HTML file. It functions as a table of contents for your PDF.

  ```
  <bookmarks>
    <bookmark name="Section 1" href="#section-1" />
    <bookmark name="Section 2" href="#section-2" />
    <bookmark name="Section 3" href="#section-3" />
  </bookmarks>
  ```

- Remember to include the `lang` and `dir` attribute in your `<html>` tag:

  ```
  <html lang="en" dir="ltr">
  ```

- Ensure that all links contain a `title` attribute to describe the link.
- Run `java -jar html-to-pdf-ua.jar "path/to/your/html/file"` on your console.
- Append `pdf/a-4` to the command to use PDF/A-4 standards instead of PDF/A-3a.
- A file called `output.pdf` will be generated in the same folder as your HTML file.

## Compilation

Make sure you have a Java Development Kit _(1.8 and above)_ and Apache Maven installed.

I used a slightly modified copy of OpenHTMLtoPDF 1.0.10 _(the latest version at the time of writing)_. The PDFs created using the official OpenHTMLtoPDF 1.0.10 library were violating [Clause: 7.18.5 of PDF/UA](https://github.com/veraPDF/veraPDF-validation-profiles/wiki/PDFUA-Part-1-rules#rule-7185-2).

- Download the source code for [OpenHTMLtoPDF 1.0.10](https://github.com/danfickle/openhtmltopdf/releases/tag/openhtmltopdf-parent-1.0.10) (Later versions might work too).
- Extract it and open your console in the created folder.
- Do `git init`.
- Copy the [patches](https://github.com/jialiang/HTML-to-PDF-UA/tree/master/openhtmltopdf/) into the folder and apply them by doing `git am *.patch`.
- Run `mvn clean install` to compile, test, package and install it into your local Maven repository.
- Clone this repository.
- Run `mvn clean package` to compile, test and package this project.

## Warning

Don't expect to plug in any random HTML file and receive a nice PDF.

OpenHTMLtoPDF uses its own engine to render the HTML file. So expect email client level feature support _(e.g. flexbox and calc are not supported)_ and plenty of inconsistencies with mainstream browsers.

Chances are, you'll need to rebuild your HTML to accommodate it.

As unfortunate as the situation is, I've yet to find anything free that's better than OpenHTMLtoPDF for creating PDF/UA compliant PDFs from HTML.
