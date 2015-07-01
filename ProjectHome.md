Track info about this at forum http://www.mobileread.com/forums/showthread.php?t=28707

Usage:
java -Xms200M -Xmx300M -jar LRFTools-vx.y.z.jar action (params)

Note: Java VM gets 200MB minimun, 300 maximun memory to work. If you plan to
convert only a few books, you can reduce significantly these values (40,60).

Actions:

1) convertLRF dir (-d dirOut |-z zipfile) (-XML|-PDF|-RTF|-HTML|-EPUB) (options)

> Converts to XML, PDF, RTF and/or HTML every LRF file in 'dir' recursively.
> You can specify more than one format conversion simultaneously.
> Converted files go to the same place as original, except if you specify
> '-z zipfile', then go to zipfile; or '-d dir', then output files are created
> in dirOut replicating the same original directory tree structure.

(options) one or more of:

> -A4 : Means normal paper size (trying to reflow)

> -rf nn : Fonts Sizes, nn is a percentage of resistance to change size of font

> -catpar str: Try to merge paragraphs not ending with '.', ':','?' this way:
> > para1+str+para2


> -repl "str1" "str2" : Replace instances of str1 (in a block) for str2

> -noo : No overwrite existing files.

> -noe : No Embed OTF fonts on epub files.

> -nopb: Do not emit page breaks.

> You cannot output HTML files to a zipfile right now.

2) convertPDF dir (-d dirOut) [-noo|-SVG|-noe|-nopb]

> Converts every PDF in 'dir' (with recursion) to EPUB format.
> -SVG means use batik to generate Scalable Vector Graphics for each page
> Other options same as convertLRF.

3) convertDOCX dir (-d dirOut) [-noo|-noe|nopb]

> Converts every OpenXML file (Word 2007 docx files) to EPUB format.
> Other options same as convertLRF.

4) view

> Shows GUI to view epub files.

5) updfmd  dir

> Search dir recursively, expecting PDF filenames 'BookAuthor-BookTitle.pdf'
> and modifies metadata of PDF to reflect BookAuthor and BookTitle so our
> Sony PRS can catalog the book conveniently.

6) mergePDF dir -o grouped.pdf

> Merge all PDF and LRF files contained at dir (with recursion) into one new
> PDF file with a TOC pointing to each old single document. LRF files are
> converted first to PDF.

7) mergeEPUB dir -a author -t title -o grouped.epub

> Merge all EPUB files contained at dir (with recursion) into one new EPUB
> file with a TOC pointing to each old single document.
