package being.gaoyuan.encodingdetect;
//https://www.garykessler.net/library/file_sigs.html

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;


public class BinaryType extends MagicNumbers {
    private final int magicNumberOffset;
    private final String extensionsText;
    private final String[] extensions;
    public final String description;

    public static final BinaryType UNKNOWN_BINARY = new BinaryType();
    private static final Multimap<String, BinaryType> DEFINED_TYPES;

    private static class PreDefines {
        public static final BinaryType AAC = new BinaryType("AAC", "FF F1", "MPEG-4 Advanced Audio Coding (AAC) Low Complexity (LC) audio file");
        public static final BinaryType AAC_0 = new BinaryType("AAC", "FF F9", "MPEG-2 Advanced Audio Coding (AAC) Low Complexity (LC) audio file");
        public static final BinaryType ABA = new BinaryType("ABA", "00 01 42 41", "Palm Address Book Archive file");
        public static final BinaryType ABD_QSD = new BinaryType("ABD,QSD", "51 57 20 56 65 72 2E 20", "Quicken data file");
        public static final BinaryType ABI = new BinaryType("ABI", "41 4F 4C 49 4E 44 45 58", "AOL address book index file");
        public static final BinaryType ABY_IDX = new BinaryType("ABY,IDX", "41 4F 4C 44 42", "AOL database files: address book (ABY) and user configuration data (MAIN.IDX)");
        public static final BinaryType ACCDB = new BinaryType("ACCDB", "00 01 00 00 53 74 61 6E 64 61 72 64 20 41 43 45 20 44 42", "Microsoft Access 2007 file");
        public static final BinaryType ACD = new BinaryType("ACD", "72 69 66 66", "Sonic Foundry Acid Music File (Sony)");
        public static final BinaryType ACM = new BinaryType("ACM", "4D 5A", "MS audio compression manager driver");
        public static final BinaryType ACS = new BinaryType("ACS", "C3 AB CD AB", "MS Agent Character file");
        public static final BinaryType AC_ = new BinaryType("AC_", "D0 CF 11 E0 A1 B1 1A E1", "CaseWare Working Papers compressed client file");
        public static final BinaryType ADF = new BinaryType("ADF", "44 4F 53", "Amiga disk file");
        public static final BinaryType ADF_0 = new BinaryType("ADF", "52 45 56 4E 55 4D 3A 2C", "Antenna data file");
        public static final BinaryType ADP = new BinaryType("ADP", "D0 CF 11 E0 A1 B1 1A E1", "Access project file");
        public static final BinaryType ADX = new BinaryType("ADX", "03 00 00 00 41 50 50 52", "Approach index file");
        public static final BinaryType ADX_0 = new BinaryType("ADX", "80 00", "ADX lossy compressed audio file");
        public static final BinaryType AES = new BinaryType("AES", "41 45 53", "AES Crypt file format. (The fourth byte is the version number.)");
        public static final BinaryType AIFF = new BinaryType("AIFF", "46 4F 52 4D 00", "Audio Interchange File");
        public static final BinaryType AIN = new BinaryType("AIN", "21 12", "AIN Compressed Archive");
        public static final BinaryType AMR = new BinaryType("AMR", "23 21 41 4D 52", "Adaptive Multi-Rate ACELP (Algebraic Code Excited Linear Prediction) Codec, commonly audio format with GSM cell phones. (See RFC 4867.)");
        public static final BinaryType ANI = new BinaryType("ANI", "52 49 46 46", "Windows animated cursor");
        public static final BinaryType ANM = new BinaryType("ANM", "46 4F 52 4D", "IFF ANIM (Amiga delta/RLE encoded bitmap animation) file");
        public static final BinaryType ANM_0 = new BinaryType("ANM", "4C 50 46 20 00 01", "DeluxePaint Animation file");
        public static final BinaryType API = new BinaryType("API", "4D 5A 90 00 03 00 00 00", "Acrobat plug-in");
        public static final BinaryType APK = new BinaryType("APK", "50 4B 03 04", "Android package");
        public static final BinaryType APR = new BinaryType("APR", "D0 CF 11 E0 A1 B1 1A E1", "Lotus/IBM Approach 97 file");
        public static final BinaryType APUF = new BinaryType("APUF", "42 65 67 69 6E 20 50 75 66 66 65 72 20 44 61 74 61 0D 0A", "Puffer ASCII-armored encrypted archive");
        public static final BinaryType ARC = new BinaryType("ARC", "41 72 43 01", "FreeArc compressed file");
        public static final BinaryType ARF = new BinaryType("ARF", "01 00 02 00", "Webex Advanced Recording Format files.");
        public static final BinaryType ARJ = new BinaryType("ARJ", "60 EA", "Compressed archive file");
        public static final BinaryType ARL_AUT = new BinaryType("ARL,AUT", "D4 2A", "AOL history (ARL) and typed URL (AUT) files");
        public static final BinaryType ART = new BinaryType("ART", "4A 47 04 0E", "AOL ART file Trailers: For 0x4A-47-03-0E: D0 CB 00 00 (��..) For 0x4A-47-04-0E: CF C7 CB (���)");
        public static final BinaryType ASF_WMA_WMV = new BinaryType("ASF,WMA,WMV", "30 26 B2 75 8E 66 CF 11 A6 D9 00 AA 00 62 CE 6C", "Microsoft Windows Media Audio/Video File (Advanced Systems Format)");
        public static final BinaryType AST = new BinaryType("AST", "53 43 48 6C", "Need for Speed: Underground Audio file");
        public static final BinaryType ASX = new BinaryType("ASX", "3C", "Advanced Stream redirector file");
        public static final BinaryType ATTACHMENT = new BinaryType("attachment", "4F 50 43 4C 44 41 54", "1Password 4 Cloud Keychain encrypted attachment");
        public static final BinaryType AU = new BinaryType("AU", "2E 73 6E 64", "NeXT/Sun Microsystems �-Law audio file");
        public static final BinaryType AU_0 = new BinaryType("AU", "64 6E 73 2E", "Audacity audio file");
        public static final BinaryType AW = new BinaryType("AW", "8A 01 09 00 00 00 E1 08 00 00 99 19", "MS Answer Wizard file");
        public static final BinaryType B64 = new BinaryType("b64", "62 65 67 69 6E 2D 62 61 73 65 36 34", "UUencoded BASE64 file Trailer: 0A 3D 3D 3D 3D 0A (.====.)");
        public static final BinaryType B85 = new BinaryType("B85", "3C 7E 36 3C 5C 25 5F 30 67 53 71 68 3B", "ASCII85 (aka BASE85) encoded file, sometimes used with PostScript and PDF. Trailer: 7E 3E 0A (~>.)");
        public static final BinaryType BAG = new BinaryType("BAG", "41 4F 4C 20 46 65 65 64 62 61 67", "AOL and AIM buddy list file");
        public static final BinaryType BDR = new BinaryType("BDR", "58 54", "MS Publisher border");
        public static final BinaryType BLI = new BinaryType("BLI", "42 6C 69 6E 6B 20 62 79 20 44 2E 54 2E 53", "BLINK compressed archive");
        public static final BinaryType BLI_RBI = new BinaryType("BLI,RBI", "42 4C 49 32 32 33", "Thomson Speedtouch series WLAN router firmware");
        public static final BinaryType BMP_DIB = new BinaryType("BMP,DIB", "42 4D", "Windows (or device-independent) bitmap image NOTE: Bytes 2-5 contain the file length in little-endian order.");
        public static final BinaryType BPG = new BinaryType("BPG", "42 50 47 FB", "Better Portable Graphics image format");
        public static final BinaryType BSB = new BinaryType("BSB", "21", "MapInfo Sea Chart");
        public static final BinaryType BZ2_TAR_BZ2_TBZ2_TB2 = new BinaryType("BZ2,TAR_BZ2,TBZ2,TB2", "42 5A 68", "bzip2 compressed archive");
        public static final BinaryType CAB = new BinaryType("CAB", "4D 53 43 46", "Microsoft cabinet file");
        public static final BinaryType CAB_HDR = new BinaryType("CAB,HDR", "49 53 63 28", "Install Shield v5.x or 6.x compressed file");
        public static final BinaryType CAF = new BinaryType("CAF", "63 61 66 66", "Apple Core Audio File");
        public static final BinaryType CAL = new BinaryType("CAL", "53 75 70 65 72 43 61 6C 63", "SuperCalc worksheet");
        public static final BinaryType CAL_0 = new BinaryType("CAL", "73 72 63 64 6F 63 69 64 3A", "CALS raster bitmap file");
        public static final BinaryType CAL_1 = new BinaryType("CAL", "B5 A2 B0 B3 B3 B0 A5 B5", "Windows calendar file");
        public static final BinaryType CAP = new BinaryType("CAP", "52 54 53 53", "Windows NT Netmon capture file");
        public static final BinaryType CAP_0 = new BinaryType("CAP", "58 43 50 00", "Cinco NetXRay, Network General Sniffer, and Network Associates Sniffer capture file");
        public static final BinaryType CAS_CBK = new BinaryType("CAS,CBK", "5F 43 41 53 45 5F", "EnCase case file (and backup)");
        public static final BinaryType CAT = new BinaryType("CAT", "30", "Microsoft security catalog file");
        public static final BinaryType CBD = new BinaryType("CBD", "43 42 46 49 4C 45", "WordPerfect dictionary file (unconfirmed)");
        public static final BinaryType CDR = new BinaryType("CDR", "45 4C 49 54 45 20 43 6F 6D 6D 61 6E 64 65 72 20", "Elite Plus Commander saved game file");
        public static final BinaryType CDR_0 = new BinaryType("CDR", "52 49 46 46", "CorelDraw document");
        public static final BinaryType CDR_DVF = new BinaryType("CDR,DVF", "4D 53 5F 56 4F 49 43 45", "Sony Compressed Voice File");
        public static final BinaryType CFG = new BinaryType("CFG", "5B 66 6C 74 73 69 6D 2E 30 5D", "Flight Simulator Aircraft Configuration file");
        public static final BinaryType CHI_CHM = new BinaryType("CHI,CHM", "49 54 53 46", "Microsoft Compiled HTML Help File");
        public static final BinaryType CIF = new BinaryType("CIF", 2, "5B 56 65 72 73 69 6F 6E", "(Unknown file type)");
        public static final BinaryType CIN = new BinaryType("CIN", "43 61 6C 63 75 6C 75 78 20 49 6E 64 6F 6F 72 20", "Calculux Indoor lighting design software project file");
        public static final BinaryType CIN_0 = new BinaryType("CIN", "80 2A 5F D7", "Kodak Cineon image file");
        public static final BinaryType CL5 = new BinaryType("CL5", "10 00 00 00", "Easy CD Creator 5 Layout file");
        public static final BinaryType CLASS = new BinaryType("CLASS", "CA FE BA BE", "Java bytecode file (also used by Apple iOS apps)");
        public static final BinaryType CLB = new BinaryType("CLB", "43 4D 58 31", "Corel Binary metafile");
        public static final BinaryType CLB_0 = new BinaryType("CLB", "43 4F 4D 2B", "COM+ Catalog file");
        public static final BinaryType CMX = new BinaryType("CMX", "52 49 46 46", "Corel Presentation Exchange (Corel 10 CMX) Metafile");
        public static final BinaryType CNV = new BinaryType("CNV", "53 51 4C 4F 43 4F 4E 56 48 44 00 00 31 2E 30 00", "DB2 conversion file");
        public static final BinaryType COD = new BinaryType("COD", "4E 61 6D 65 3A 20", "Agent newsreader character map file");
        public static final BinaryType COM_DLL_DRV_EXE_PIF_QTS_QTX_SYS = new BinaryType("COM,DLL,DRV,EXE,PIF,QTS,QTX,SYS", "4D 5A", "Windows/DOS executable file (See The MZ EXE File Format page for the structure of an EXE file, with coverage of NE, TLINK, PE, self-extracting archives, and more.) Note: MZ are the initals of Mark Zbikowski, designer of the DOS executable file format.");
        public static final BinaryType COM_SYS = new BinaryType("COM,SYS", "EB", "Windows executable file");
        public static final BinaryType CPE = new BinaryType("CPE", "46 41 58 43 4F 56 45 52 2D 56 45 52", "Microsoft Fax Cover Sheet");
        public static final BinaryType CPI = new BinaryType("CPI", "53 49 45 54 52 4F 4E 49 43 53 20 58 52 44 20 53 43 41 4E", "Sietronics CPI XRD document");
        public static final BinaryType CPI_0 = new BinaryType("CPI", "FF 46 4F 4E 54", "Windows international code page");
        public static final BinaryType CPL = new BinaryType("CPL", "DC DC", "Corel color palette file");
        public static final BinaryType CPT = new BinaryType("CPT", "43 50 54 37 46 49 4C 45", "Corel Photopaint file");
        public static final BinaryType CPT_0 = new BinaryType("CPT", "43 50 54 46 49 4C 45", "Corel Photopaint file");
        public static final BinaryType CPX = new BinaryType("CPX", "5B 57 69 6E 64 6F 77 73 20 4C 61 74 69 6E 20", "Microsoft Code Page Translation file");
        public static final BinaryType CR2 = new BinaryType("CR2", "49 49 2A 00 10 00 00 00 43 52", "Canon digital camera RAW file");
        public static final BinaryType CRU = new BinaryType("CRU", "43 52 55 53 48 20 76", "Crush compressed archive");
        public static final BinaryType CRW = new BinaryType("CRW", "49 49 1A 00 00 00 48 45 41 50 43 43 44 52 02 00", "Canon digital camera RAW file");
        public static final BinaryType CRX = new BinaryType("CRX", "43 72 32 34", "Google Chrome Extension");
        public static final BinaryType CSD = new BinaryType("CSD", "3C 43 73 6F 75 6E 64 53 79 6E 74 68 65 73 69 7A", "Csound music file");
        public static final BinaryType CSD_0 = new BinaryType("CSD", "7C 4B C3 74 E1 C8 53 A4 79 B9 01 1D FC 4F DD 13", "Huskygram, Poem, or Singer embroidery design file");
        public static final BinaryType CSH = new BinaryType("CSH", "63 75 73 68 00 00 00 02 00 00 00", "Photoshop Custom Shape");
        public static final BinaryType CSO = new BinaryType("CSO", "43 49 53 4F", "Compressed ISO (CISO) CD image");
        public static final BinaryType CTF = new BinaryType("CTF", "43 61 74 61 6C 6F 67 20 33 2E 30 30 00", "WhereIsIt Catalog file");
        public static final BinaryType CTL = new BinaryType("CTL", "56 45 52 53 49 4F 4E 20", "Visual Basic User-defined Control file");
        public static final BinaryType CUR = new BinaryType("CUR", "00 00 02 00", "Windows cursor file");
        public static final BinaryType DAA = new BinaryType("DAA", "44 41 41 00 00 00 00 00", "PowerISO Direct-Access-Archive image");
        public static final BinaryType DAT = new BinaryType("DAT", 8, "00 00 00 00 62 31 05 00 09 00 00 00 00 20 00 00 00 09 00 00 00 00 00 00", "Bitcoin Core wallet.dat file");
        public static final BinaryType DAT_0 = new BinaryType("DAT", "03", "MapInfo Native Data Format");
        public static final BinaryType DAT_1 = new BinaryType("DAT", "1A 52 54 53 20 43 4F 4D 50 52 45 53 53 45 44 20 49 4D 41 47 45 20 56 31 2E 30 1A", "Runtime Software compressed disk image");
        public static final BinaryType DAT_10 = new BinaryType("DAT", "50 4D 4F 43 43 4D 4F 43", "Microsoft� Windows� User State Migration Tool (USMT). USMT 3.0 applies to Windows XP and Windows Vista�, and USMT 4.0 is for Windows 7.");
        public static final BinaryType DAT_11 = new BinaryType("DAT", "52 41 5A 41 54 44 42 31", "Shareaza (Windows P2P client) thumbnail");
        public static final BinaryType DAT_12 = new BinaryType("DAT", "52 49 46 46", "Video CD MPEG or MPEG1 movie file");
        public static final BinaryType DAT_13 = new BinaryType("DAT", "55 46 4F 4F 72 62 69 74", "UFO Capture v2 map file");
        public static final BinaryType DAT_14 = new BinaryType("DAT", "57 4D 4D 50", "Walkman MP3 container file");
        public static final BinaryType DAT_15 = new BinaryType("DAT", "72 65 67 66", "Windows NT registry hive file");
        public static final BinaryType DAT_16 = new BinaryType("DAT", "73 6C 68 2E", "Allegro Generic Packfile Data file (uncompressed)");
        public static final BinaryType DAT_17 = new BinaryType("DAT", "A9 0D 00 00 00 00 00 00", "Access Data FTK evidence file");
        public static final BinaryType DAT_18 = new BinaryType("DAT", "BE BA FE CA 0F 50 61 6C 6D 53 47 20 44 61 74 61", "Palm Desktop DateBook file");
        public static final BinaryType DAT_19 = new BinaryType("DAT", "F9 BE B4 D9", "Bitcoin-Qt blockchain block file");
        public static final BinaryType DAT_2 = new BinaryType("DAT", "41 56 47 36 5F 49 6E 74 65 67 72 69 74 79 5F 44 61 74 61 62 61 73 65", "AVG6 Integrity database file");
        public static final BinaryType DAT_3 = new BinaryType("DAT", "43 52 45 47", "Windows 9x registry hive");
        public static final BinaryType DAT_4 = new BinaryType("DAT", "43 6C 69 65 6E 74 20 55 72 6C 43 61 63 68 65 20 4D 4D 46 20 56 65 72 20", "Microsoft Internet Explorer cache file (index.dat) file");
        public static final BinaryType DAT_5 = new BinaryType("DAT", "45 52 46 53 53 41 56 45 44 41 54 41 46 49 4C 45", "Kroll EasyRecovery Saved Recovery State file");
        public static final BinaryType DAT_6 = new BinaryType("DAT", "49 6E 6E 6F 20 53 65 74 75 70 20 55 6E 69 6E 73 74 61 6C 6C 20 4C 6F 67 20 28 62 29", "Inno Setup Uninstall Log file");
        public static final BinaryType DAT_7 = new BinaryType("DAT", "4E 41 56 54 52 41 46 46 49 43", "TomTom traffic data file");
        public static final BinaryType DAT_8 = new BinaryType("DAT", "50 45 53 54", "PestPatrol data/scan strings");
        public static final BinaryType DAT_9 = new BinaryType("DAT", "50 4E 43 49 55 4E 44 4F", "Norton Disk Doctor undo file");
        public static final BinaryType DAX = new BinaryType("DAX", "44 41 58 00", "DAX Compressed CD image");
        public static final BinaryType DAX_0 = new BinaryType("DAX", "46 4F 52 4D 00", "DAKX Compressed Audio");
        public static final BinaryType DB3 = new BinaryType("DB3", "03", "dBASE III file");
        public static final BinaryType DB4 = new BinaryType("DB4", "04", "dBASE IV data file");
        public static final BinaryType DBA = new BinaryType("DBA", "00 01 42 44", "Palm DateBook Archive file");
        public static final BinaryType DBB = new BinaryType("DBB", "6C 33 33 6C", "Skype user data file (profile and contacts)");
        public static final BinaryType DBF = new BinaryType("DBF", "4F 50 4C 44 61 74 61 62 61 73 65 46 69 6C 65", "Psion Series 3 Database file");
        public static final BinaryType DBX = new BinaryType("DBX", "CF AD 12 FE", "Outlook Express e-mail folder");
        public static final BinaryType DCI = new BinaryType("DCI", "3C 21 64 6F 63 74 79 70", "AOL HTML mail file");
        public static final BinaryType DCX = new BinaryType("DCX", "B1 68 DE 3A", "Graphics Multipage PCX bitmap file");
        public static final BinaryType DEX = new BinaryType("dex", "64 65 78 0A", "Dalvik executable file (Android)");
        public static final BinaryType DMG = new BinaryType("DMG", "42 5A 68", "Mac Disk image (BZ2 compressed)");
        public static final BinaryType DMG_0 = new BinaryType("DMG", "63 64 73 61 65 6E 63 72", "Macintosh encrypted Disk image (v1)");
        public static final BinaryType DMG_1 = new BinaryType("DMG", "65 6E 63 72 63 64 73 61", "Macintosh encrypted Disk image (v2)");
        public static final BinaryType DMG_2 = new BinaryType("DMG", "78 01 73 0D 62 62 60", "Mac OS X Disk Copy Disk Image file");
        public static final BinaryType DMP = new BinaryType("DMP", "4D 44 4D 50 93 A7", "Windows minidump file");
        public static final BinaryType DMP_0 = new BinaryType("DMP", "50 41 47 45 44 55 36 34", "Windows 64-bit memory dump");
        public static final BinaryType DMP_1 = new BinaryType("DMP", "50 41 47 45 44 55 4D 50", "Windows memory dump");
        public static final BinaryType DMS = new BinaryType("DMS", "44 4D 53 21", "Amiga DiskMasher compressed archive");
        public static final BinaryType DOC = new BinaryType("DOC", "0D 44 4F 43", "DeskMate Document file");
        public static final BinaryType DOCX_PPTX_XLSX = new BinaryType("DOCX,PPTX,XLSX", "50 4B 03 04 14 00 06 00", "Microsoft Office Open XML Format (OOXML) Document NOTE: There is no subheader for MS OOXML files as there is with DOC, PPT, and XLS files. To better understand the format of these files, rename any OOXML file to have a .ZIP extension and then unZIP the file; look at the resultant file named [Content_Types].xml to see the content types. In particular, look for the <Override PartName= tag, where you will find word, ppt, or xl, respectively. Trailer: Look for 50 4B 05 06 (PK..) followed by 18 additional bytes at the end of the file.");
        public static final BinaryType DOC_0 = new BinaryType("DOC", "CF 11 E0 A1 B1 1A E1 00", "Perfect Office document [Note similarity to MS Office header, below]");
        public static final BinaryType DOC_1 = new BinaryType("DOC", "DB A5 2D 00", "Word 2.0 file");
        public static final BinaryType DOC_2 = new BinaryType("DOC", 512, "EC A5 C1 00", "Word document subheader (MS Office)");
        public static final BinaryType DOC_DOT_PPS_PPT_XLA_XLS_WIZ = new BinaryType("DOC,DOT,PPS,PPT,XLA,XLS,WIZ", "D0 CF 11 E0 A1 B1 1A E1", "An Object Linking and Embedding (OLE) Compound File (CF) (i.e., OLECF) file format, known as Compound Binary File format by Microsoft, used by Microsoft Office 97-2003 applications (Word, Powerpoint, Excel, Wizard). [See also Excel, Outlook, PowerPoint, and Word \"subheaders\" at byte offset 512 (0x200).] There appear to several subheader formats and a dearth of documentation. There have been reports that there are different subheaders for Windows and Mac versions of MS Office but I cannot confirm that.] Password-protected DOCX, XLSX, and PPTX files also use this signature those files are saved as OLECF files. [Note the similarity between D0 CF 11 E0 and the word \"DOCFILE\"!]");
        public static final BinaryType DPX = new BinaryType("DPX", "53 44 50 58", "Society of Motion Picture and Television Engineers (SMPTE) Digital Picture Exchange (DPX) image file (big endian)");
        public static final BinaryType DPX_0 = new BinaryType("DPX", "58 50 44 53", "Society of Motion Picture and Television Engineers (SMPTE) Digital Picture Exchange (DPX) image file (little endian)");
        public static final BinaryType DRW = new BinaryType("DRW", "01 FF 02 04 03 02", "Micrografx vector graphic file");
        public static final BinaryType DRW_0 = new BinaryType("DRW", "07", "A common signature and file extension for many drawing programs.");
        public static final BinaryType DS4 = new BinaryType("DS4", "52 49 46 46", "Micrografx Designer v4 graphic file");
        public static final BinaryType DSF = new BinaryType("DSF", "44 53 44 20", "DSD Storage Facility audio file");
        public static final BinaryType DSF_0 = new BinaryType("DSF", "50 53 46 12", "Dreamcast Sound Format file, a subset of the Portable Sound Format.");
        public static final BinaryType DSN = new BinaryType("DSN", "4D 56", "CD Stomper Pro label file");
        public static final BinaryType DSP = new BinaryType("DSP", "23 20 4D 69 63 72 6F 73 6F 66 74 20 44 65 76 65 6C 6F 70 65 72 20 53 74 75 64 69 6F", "Microsoft Developer Studio project file");
        public static final BinaryType DSS = new BinaryType("DSS", "02 64 73 73", "Digital Speech Standard (Olympus, Grundig, & Phillips)");
        public static final BinaryType DST = new BinaryType("DST", "44 53 54 62", "DST compressed file");
        public static final BinaryType DST_0 = new BinaryType("DST", "4C 41 3A", "Tajima embroidery sticj image file");
        public static final BinaryType DSW = new BinaryType("DSW", "64 73 77 66 69 6C 65", "Microsoft Visual Studio workspace file");
        public static final BinaryType DTD = new BinaryType("DTD", "07 64 74 32 64 64 74 64", "DesignTools 2D Design file");
        public static final BinaryType DUN = new BinaryType("DUN", "5B 50 68 6F 6E 65 5D", "Dial-up networking file");
        public static final BinaryType DVR = new BinaryType("DVR", "44 56 44", "DVR-Studio stream file");
        public static final BinaryType DW4 = new BinaryType("DW4", "4F 7B", "Visio/DisplayWrite 4 text file (unconfirmed)");
        public static final BinaryType DWG = new BinaryType("DWG", "41 43", "Generic AutoCAD drawing file");
        public static final BinaryType ECF = new BinaryType("ECF", "5B 47 65 6E 65 72 61 6C 5D 0D 0A 44 69 73 70 6C 61 79 20 4E 61 6D 65 3D 3C 44 69 73 70 6C 61 79 4E 61 6D 65", "MS Exchange 2007 extended configuration file");
        public static final BinaryType EFX = new BinaryType("EFX", "DC FE", "eFax file format");
        public static final BinaryType EMF = new BinaryType("EMF", "01 00 00 00", "Extended (Enhanced) Windows Metafile Format, printer spool file (0x18-17 & 0xC4-36 is Win2K/NT; 0x5C0-1 is WinXP)");
        public static final BinaryType EML = new BinaryType("EML", "46 72 6F 6D 3A 20", "A commmon file extension for e-mail files. Signatures shown here are for Netscape, Eudora, and a generic signature, respectively. EML is also used by Outlook Express and QuickMail.");
        public static final BinaryType EML_0 = new BinaryType("EML", "52 65 74 75 72 6E 2D 50 61 74 68 3A 20", "A commmon file header for e-mail files.");
        public static final BinaryType EML_1 = new BinaryType("EML", "58 2D", "A commmon file extension for e-mail files. This variant is for Exchange.");
        public static final BinaryType ENC = new BinaryType("ENC", "00 5C 41 B1 FF", "Mujahideen Secrets 2 encrypted file");
        public static final BinaryType ENL = new BinaryType("ENL", 32, "40 40 40 20 00 00 40 40 40 40", "EndNote Library File");
        public static final BinaryType EPS = new BinaryType("EPS", "25 21 50 53 2D 41 64 6F 62 65 2D 33 2E 30 20 45 50 53 46 2D 33 20 30", "Adobe encapsulated PostScript file (If this signature is not at the immediate beginning of the file, it will occur early in the file, commonly at byte offset 30 [0x1E])");
        public static final BinaryType EPS_0 = new BinaryType("EPS", "C5 D0 D3 C6", "Adobe encapsulated PostScript file");
        public static final BinaryType EPUB = new BinaryType("EPUB", "50 4B 03 04 0A 00 02 00", "Open Publication Structure eBook file. (Should also include the string: mimetypeapplication/epub+zip)");
        public static final BinaryType ESD = new BinaryType("ESD", "7E 45 53 44 77 F6 85 3E BF 6A D2 11 45 61 73 79 20 53 74 72 65 65 74 20 44 72 61 77", "Easy Street Draw diagram file");
        public static final BinaryType ETH = new BinaryType("ETH", "1A 35 01 00", "GN Nettest WinPharoah capture file");
        public static final BinaryType EVT = new BinaryType("EVT", "30 00 00 00 4C 66 4C 65", "Windows Event Viewer file");
        public static final BinaryType EVTX = new BinaryType("EVTX", "45 6C 66 46 69 6C 65 00", "Windows Vista event log file");
        public static final BinaryType EXR = new BinaryType("EXR", "76 2F 31 01", "OpenEXR bitmap image format");
        public static final BinaryType FBM = new BinaryType("FBM", "25 62 69 74 6D 61 70", "Fuzzy bitmap (FBM) file");
        public static final BinaryType FDB = new BinaryType("FDB", "46 44 42 48 00", "Fiasco database definition file");
        public static final BinaryType FDB_GDB = new BinaryType("FDB,GDB", "01 00 39 30", "Firebird and Interbase database files, respectively. See IBPhoenix for more information.");
        public static final BinaryType FITS = new BinaryType("FITS", "53 49 4D 50 4C 45 20 20 3D 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 20 54", "Flexible Image Transport System (FITS), Version 3.0 file");
        public static final BinaryType FLAC = new BinaryType("FLAC", "66 4C 61 43 00 00 00 22", "Free Lossless Audio Codec file");
        public static final BinaryType FLI = new BinaryType("FLI", "00 11 AF", "FLIC Animation file");
        public static final BinaryType FLT = new BinaryType("FLT", "00 01 01", "OpenFlight 3D file");
        public static final BinaryType FLT_0 = new BinaryType("FLT", "76 32 30 30 33 2E 31 30 0D 0A 30 0D 0A", "Qimage filter");
        public static final BinaryType FLV = new BinaryType("FLV", "46 4C 56 01", "Flash video file");
        public static final BinaryType FLV_M4V = new BinaryType("FLV,M4V", 4, "66 74 79 70 4D 34 56 20", "ISO Media, MPEG v4 system, or iTunes AVC-LC file");
        public static final BinaryType FM_MIF = new BinaryType("FM,MIF", "3C 4D 61 6B 65 72 46 69 6C 65 20", "Adobe FrameMaker file");
        public static final BinaryType FTR = new BinaryType("FTR", "D2 0A 00 00", "GN Nettest WinPharoah filter file");
        public static final BinaryType G64 = new BinaryType("G64", "47 65 6E 65 74 65 63 20 4F 6D 6E 69 63 61 73 74", "Genetec video archive");
        public static final BinaryType GDB = new BinaryType("GDB", "4D 73 52 63 66", "VMapSource GPS Waypoint Database");
        public static final BinaryType GED = new BinaryType("GED", "30 20 48 45 41 44", "GEnealogical Data COMmunication (GEDCOM) file");
        public static final BinaryType GHO_GHS = new BinaryType("GHO,GHS", "FE EF", "Symantex Ghost image file");
        public static final BinaryType GID = new BinaryType("GID", "3F 5F 03 00", "Windows Help index file");
        public static final BinaryType GID_0 = new BinaryType("GID", "4C 4E 02 00", "Windows Help index file");
        public static final BinaryType GIF = new BinaryType("GIF", "47 49 46 38 39 61", "Graphics interchange format file Trailer: 00 3B (.;)");
        public static final BinaryType GPG = new BinaryType("GPG", "99", "GNU Privacy Guard (GPG) public keyring");
        public static final BinaryType GPX = new BinaryType("GPX", "3C 67 70 78 20 76 65 72 73 69 6F 6E 3D 22 31 2E 31", "GPS eXchange file (v1.1 schema)");
        public static final BinaryType GRB = new BinaryType("GRB", "47 52 49 42", "GRIdded Binary or General Regularly-distributed Information in Binary file, commonly used in meteorology to store historical and forecast weather data");
        public static final BinaryType GRP = new BinaryType("GRP", "50 4D 43 43", "Windows Program Manager group file");
        public static final BinaryType GX2 = new BinaryType("GX2", "47 58 32", "Show Partner graphics file (not confirmed)");
        public static final BinaryType GZ_TGZ = new BinaryType("GZ,TGZ", "1F 8B 08", "GZIP archive file");
        public static final BinaryType HAP = new BinaryType("HAP", "91 33 48 46", "Hamarsoft HAP 3.x compressed archive");
        public static final BinaryType HDMP = new BinaryType("HDMP", "4D 44 4D 50 93 A7", "Windows heap dump file");
        public static final BinaryType HDR = new BinaryType("HDR", "23 3F 52 41 44 49 41 4E 43 45 0A", "Radiance High Dynamic Range image file");
        public static final BinaryType HLP = new BinaryType("HLP", 6, "00 00 FF FF FF FF", "Windows Help file");
        public static final BinaryType HLP_0 = new BinaryType("HLP", "3F 5F 03 00", "Windows Help file");
        public static final BinaryType HLP_1 = new BinaryType("HLP", "4C 4E 02 00", "Windows Help file");
        public static final BinaryType HQX = new BinaryType("HQX", "28 54 68 69 73 20 66 69 6C 65 20 6D 75 73 74 20 62 65 20 63 6F 6E 76 65 72 74 65 64 20 77 69 74 68 20 42 69 6E 48 65 78 20", "Macintosh BinHex 4 Compressed Archive");
        public static final BinaryType HUS = new BinaryType("HUS", "5D FC C8 00", "Husqvarna Designer I Embroidery Machine file");
        public static final BinaryType ICO = new BinaryType("ICO", "00 00 01 00", "Windows icon file");
        public static final BinaryType IDX = new BinaryType("IDX", "50 00 00 00 20 00 00 00", "Quicken QuickFinder Information File");
        public static final BinaryType IFF = new BinaryType("IFF", "43 41 54 20", "Electronic Arts' Interchange Format Files (IFF) format.");
        public static final BinaryType IFF_0 = new BinaryType("IFF", "46 4F 52 4D", "Electronic Arts' Interchange Format Files (IFF) format.");
        public static final BinaryType IFF_1 = new BinaryType("IFF", "4C 49 53 54", "Electronic Arts' Interchange Format Files (IFF) format.");
        public static final BinaryType IFO = new BinaryType("IFO", "44 56 44", "DVD info file");
        public static final BinaryType IMG = new BinaryType("IMG", "00 01 00 08 00 01 00 01 01", "Ventura Publisher/GEM VDI Image Format Bitmap file");
        public static final BinaryType IMG_0 = new BinaryType("IMG", "50 49 43 54 00 08", "ADEX Corp. ChromaGraph Graphics Card Bitmap Graphic file");
        public static final BinaryType IMG_1 = new BinaryType("IMG", "51 46 49 FB", "QEMU Qcow Disk Image");
        public static final BinaryType IMG_2 = new BinaryType("IMG", "53 43 4D 49", "Img Software Set Bitmap");
        public static final BinaryType IMG_3 = new BinaryType("IMG", "7E 74 2C 01 50 70 02 4D 52 01 00 00 00 08 00 00 00 01 00 00 31 00 00 00 31 00 00 00 43 01 FF 00 01 00 08 00 01 00 00 00 7e 74 2c 01", "Reportedly a proprietary recording system, possibly a Digital Watchdog DW-TP-500G unit.");
        public static final BinaryType IMG_4 = new BinaryType("IMG", "EB 3C 90 2A", "GEM Raster file");
        public static final BinaryType IND = new BinaryType("IND", "41 4F 4C 49 44 58", "AOL client preferences/settings file (MAIN.IND)");
        public static final BinaryType INDD = new BinaryType("INDD", "06 06 ED F5 D8 1D 46 E5 BD 31 EF E7 FE 74 B7 1D", "Adobe InDesign document");
        public static final BinaryType INFO = new BinaryType("INFO", "54 68 69 73 20 69 73 20", "UNIX GNU Info Reader File");
        public static final BinaryType INFO_0 = new BinaryType("INFO", "6D 75 6C 74 69 42 69 74 2E 69 6E 66 6F", "MultiBit Bitcoin wallet information file");
        public static final BinaryType INFO_1 = new BinaryType("INFO", "7A 62 65 78", "ZoomBrowser Image Index file (ZbThumbnal.info)");
        public static final BinaryType INFO_2 = new BinaryType("INFO", "E3 10 00 01 00 00 00 00", "Amiga Icon file");
        public static final BinaryType INS = new BinaryType("INS", "B8 C9 0C 00", "InstallShield Script");
        public static final BinaryType IPD = new BinaryType("IPD", "49 6E 74 65 72 40 63 74 69 76 65 20 50 61 67 65", "Inter@ctive Pager Backup (BlackBerry) backup file (See also this IPD File Format paper)");
        public static final BinaryType ISO = new BinaryType("ISO", "43 44 30 30 31", "ISO-9660 CD Disc Image This signature usually occurs at byte offset 32769 (0x8001), 34817 (0x8801), or 36865 (0x9001). More information can be found at MacTech or at ECMA.");
        public static final BinaryType ISO_0 = new BinaryType("ISO", "45 52 02 00 00", "Apple ISO 9660/HFS hybrid CD image");
        public static final BinaryType IVR = new BinaryType("IVR", "2E 52 45 43", "RealPlayer video file (V11 and later)");
        public static final BinaryType JAR = new BinaryType("JAR", "4A 41 52 43 53 00", "JARCS compressed archive");
        public static final BinaryType JAR_0 = new BinaryType("JAR", "50 4B 03 04", "Java archive; compressed file package for classes and data");
        public static final BinaryType JAR_1 = new BinaryType("JAR", "50 4B 03 04 14 00 08 00 08 00", "Java archive");
        public static final BinaryType JAR_2 = new BinaryType("JAR", "5F 27 A8 89", "Jar archive");
        public static final BinaryType JB2 = new BinaryType("JB2", "97 4A 42 32 0D 0A 1A 0A", "JBOG2 image file Trailer: 03 33 00 01 00 00 00 00 (.3......)");
        public static final BinaryType JCEKS = new BinaryType("JCEKS", "CE CE CE CE", "Java Cryptography Extension keystore file");
        public static final BinaryType JKS = new BinaryType("JKS", "FE ED FE ED", "JavaKeyStore file");
        public static final BinaryType JNT_JTP = new BinaryType("JNT,JTP", "4E 42 2A 00", "MS Windows journal file");
        public static final BinaryType JP2 = new BinaryType("JP2", "00 00 00 0C 6A 50 20 20 0D 0A", "Various JPEG-2000 image file formats");
        public static final BinaryType JPE_JPEG_JPG = new BinaryType("JPE,JPEG,JPG", "FF D8", "Generic JPEGimage file Trailer: FF D9 (��)");
        public static final BinaryType KGB = new BinaryType("KGB", "4B 47 42 5F 61 72 63 68 20 2D", "KGB archive");
        public static final BinaryType KMZ = new BinaryType("KMZ", "50 4B 03 04", "Google Earth saved working session file");
        public static final BinaryType KOZ = new BinaryType("KOZ", "49 44 33 03 00 00 00", "Sprint Music Store audio file (for mobile devices)");
        public static final BinaryType KWD = new BinaryType("KWD", "50 4B 03 04", "KWord document");
        public static final BinaryType LBK = new BinaryType("LBK", "C8 00 79 00", "Jeppesen FliteLog file");
        public static final BinaryType LGC_LGD = new BinaryType("LGC,LGD", "7B 0D 0A 6F 20", "Windows application log");
        public static final BinaryType LHA_LZH = new BinaryType("LHA,LZH", 2, "2D 6C 68", "Compressed archive file");
        public static final BinaryType LIB = new BinaryType("LIB", "21 3C 61 72 63 68 3E 0A", "Unix archiver (ar) files and Microsoft Program Library Common Object File Format (COFF)");
        public static final BinaryType LIT = new BinaryType("LIT", "49 54 4F 4C 49 54 4C 53", "Microsoft Reader eBook file");
        public static final BinaryType LNK = new BinaryType("LNK", "4C 00 00 00 01 14 02 00", "Windows shell link (shortcut) file. See also The Meaning of Linkfiles in Forensic Examinations and Evidentiary Value of Link Files.");
        public static final BinaryType LOG = new BinaryType("LOG", "2A 2A 2A 20 20 49 6E 73 74 61 6C 6C 61 74 69 6F 6E 20 53 74 61 72 74 65 64 20", "Symantec Wise Installer log file");
        public static final BinaryType LWP = new BinaryType("LWP", "57 6F 72 64 50 72 6F", "Lotus WordPro document.");
        public static final BinaryType M4A = new BinaryType("M4A", 4, "66 74 79 70 4D 34 41 20", "Apple Lossless Audio Codec file");
        public static final BinaryType M4V = new BinaryType("M4V", 4, "66 74 79 70 6D 70 34 32", "MPEG-4 video|QuickTime file");
        public static final BinaryType MANIFEST = new BinaryType("MANIFEST", "3C 3F 78 6D 6C 20 76 65 72 73 69 6F 6E 3D", "Windows Visual Stylesheet XML file");
        public static final BinaryType MAR = new BinaryType("MAR", "4D 41 52 31 00", "Mozilla archive");
        public static final BinaryType MAR_0 = new BinaryType("MAR", "4D 41 52 43", "Microsoft/MSN MARC archive");
        public static final BinaryType MAR_1 = new BinaryType("MAR", "4D 41 72 30 00", "MAr compressed archive");
        public static final BinaryType MAT = new BinaryType("MAT", "4D 41 54 4C 41 42 20 35 2E 30 20 4D 41 54 2D 66 69 6C 65", "MATLAB v5 workspace file (includes creation timestamp)");
        public static final BinaryType MDB = new BinaryType("MDB", "00 01 00 00 53 74 61 6E 64 61 72 64 20 4A 65 74 20 44 42", "Microsoft Access file");
        public static final BinaryType MDF = new BinaryType("MDF", "00 FF FF FF FF FF FF FF FF FF FF 00 00 02 00 01", "Alcohol 120% CD image");
        public static final BinaryType MDF_0 = new BinaryType("MDF", "01 0F 00 00", "Microsoft SQL Server 2000 database");
        public static final BinaryType MDI = new BinaryType("MDI", "45 50", "Microsoft Document Imaging file");
        public static final BinaryType MID_MIDI = new BinaryType("MID,MIDI", "4D 54 68 64", "Musical Instrument Digital Interface (MIDI) sound file");
        public static final BinaryType MIF = new BinaryType("MIF", "56 65 72 73 69 6F 6E 20", "MapInfo Interchange Format file");
        public static final BinaryType MKV = new BinaryType("MKV", "1A 45 DF A3", "Matroska stream file");
        public static final BinaryType MLS = new BinaryType("MLS", "4D 49 4C 45 53", "Milestones v1.0 project management and scheduling software (Also see \"MV2C\" and \"MV214\" signatures)");
        public static final BinaryType MLS_0 = new BinaryType("MLS", "4D 4C 53 57", "Skype localization data file");
        public static final BinaryType MLS_1 = new BinaryType("MLS", "4D 56 32 31 34", "Milestones v2.1b project management and scheduling software (Also see \"MILES\" and \"MV2C\" signatures)");
        public static final BinaryType MLS_2 = new BinaryType("MLS", "4D 56 32 43", "Milestones v2.1a project management and scheduling software (Also see \"MILES\" and \"MV214\" signatures)");
        public static final BinaryType MMF = new BinaryType("MMF", "4D 4D 4D 44 00 00", "Yamaha Corp. Synthetic music Mobile Application Format (SMAF) for multimedia files that can be played on hand-held devices.");
        public static final BinaryType MNY = new BinaryType("MNY", "00 01 00 00 4D 53 49 53 41 4D 20 44 61 74 61 62 61 73 65", "Microsoft Money file");
        public static final BinaryType MOF = new BinaryType("MOF", "FF FE 23 00 6C 00 69 00 6E 00 65 00 20 00 31 00", "Windows MSinfo file");
        public static final BinaryType MOV = new BinaryType("MOV", "00", "Apple QuickTime movie file");
        public static final BinaryType MOV_0 = new BinaryType("MOV", 4, "66 74 79 70 71 74 20 20", "QuickTime movie file");
        public static final BinaryType MOV_1 = new BinaryType("MOV", 4, "6D 6F 6F 76", "QuickTime movie file");
        public static final BinaryType MP = new BinaryType("MP", "0C ED", "Monochrome Picture TIFF bitmap file (unconfirmed)");
        public static final BinaryType MP3 = new BinaryType("MP3", "49 44 33", "MPEG-1 Audio Layer 3 (MP3) audio file");
        public static final BinaryType MP4 = new BinaryType("MP4", 4, "66 74 79 70 4D 53 4E 56", "MPEG-4 video file");
        public static final BinaryType MP4_0 = new BinaryType("MP4", 4, "66 74 79 70 69 73 6F 6D", "ISO Base Media file (MPEG-4) v1");
        public static final BinaryType MPG_VOB = new BinaryType("MPG,VOB", "00 00 01 BA", "DVD Video Movie File (video/dvd, video/mpeg) or DVD MPEG2 Trailer: 00 00 01 B9 (...�)");
        public static final BinaryType MSC = new BinaryType("MSC", "3C 3F 78 6D 6C 20 76 65 72 73 69 6F 6E 3D 22 31 2E 30 22 3F 3E 0D 0A 3C 4D 4D 43 5F 43 6F 6E 73 6F 6C 65 46 69 6C 65 20 43 6F 6E 73 6F 6C 65 56 65 72 73 69 6F 6E 3D 22", "Microsoft Management Console Snap-in Control file");
        public static final BinaryType MSC_0 = new BinaryType("MSC", "DB", "Microsoft Common Console Document");
        public static final BinaryType MSF = new BinaryType("MSF", "2F 2F 20 3C 21 2D 2D 20 3C 6D 64 62 3A 6D 6F 72 6B 3A 7A", "Thunderbird|Mozilla Mail Summary File");
        public static final BinaryType MSG = new BinaryType("MSG", 512, "52 00 6F 00 6F 00 74 00 20 00 45 00 6E 00 74 00 72 00 79 00", "Outlook/Exchange message subheader (MS Office)");
        public static final BinaryType MSG_0 = new BinaryType("MSG", "DB", "Microsoft Outlook/Exchange Message");
        public static final BinaryType MSI = new BinaryType("MSI", "23 20", "Cerius2 file");
        public static final BinaryType MSI_0 = new BinaryType("MSI", "DB", "Microsoft Installer package");
        public static final BinaryType MSP = new BinaryType("MSP", "DB", "Windows Installer Patch");
        public static final BinaryType MSV = new BinaryType("MSV", "4D 53 5F 56 4F 49 43 45", "Sony Memory Stick Compressed Voice file");
        public static final BinaryType MTE = new BinaryType("MTE", "4D 43 57 20 54 65 63 68 6E 6F 67 6F 6C 69 65 73", "TargetExpress target file");
        public static final BinaryType MTW = new BinaryType("MTW", "DB", "Minitab data file");
        public static final BinaryType MXD = new BinaryType("MXD", "DB", "ArcMap GIS project file");
        public static final BinaryType MXF = new BinaryType("MXF", "06 0E 2B 34 02 05 01 01 0D 01 02 01 01 02", "Material Exchange Format file");
        public static final BinaryType MXF_0 = new BinaryType("MXF", "3C 43 54 72 61 6E 73 54 69 6D 65 6C 69 6E 65 3E", "Picasa movie project file");
        public static final BinaryType NRI = new BinaryType("NRI", "0E 4E 65 72 6F 49 53 4F", "Nero CD Compilation");
        public static final BinaryType NSF = new BinaryType("NSF", "1A 00 00 04 00 00", "Lotus Notes database");
        public static final BinaryType NSF_0 = new BinaryType("NSF", "4E 45 53 4D 1A 01", "NES Sound file");
        public static final BinaryType NTF = new BinaryType("NTF", "1A 00 00", "Lotus Notes database template");
        public static final BinaryType NTF_0 = new BinaryType("NTF", "30 31 4F 52 44 4E 41 4E 43 45 20 53 55 52 56 45 59 20 20 20 20 20 20 20", "National Transfer Format Map File");
        public static final BinaryType NTF_1 = new BinaryType("NTF", "4E 49 54 46 30", "National Imagery Transmission Format (NITF) file");
        public static final BinaryType NVRAM = new BinaryType("NVRAM", "4D 52 56 4E", "VMware BIOS (non-volatile RAM) state file");
        public static final BinaryType OBJ = new BinaryType("OBJ", "4C 01", "Microsoft Common Object File Format (COFF) relocatable object code file for an Intel 386 or later/compatible processors");
        public static final BinaryType OBJ_0 = new BinaryType("OBJ", "80", "Relocatable object code");
        public static final BinaryType ODT_ODP_OTT = new BinaryType("ODT,ODP,OTT", "50 4B 03 04", "OpenDocument text document, presentation, and text document template, respectively.");
        public static final BinaryType OGA_OGG_OGV_OGX = new BinaryType("OGA,OGG,OGV,OGX", "4F 67 67 53 00 02 00 00 00 00 00 00 00 00", "Ogg Vorbis Codec compressed Multimedia file");
        public static final BinaryType ONE = new BinaryType("ONE", "E4 52 5C 7B 8C D8 A7 4D AE B1 53 78 D0 29 96 D3", "Microsoft OneNote note");
        public static final BinaryType ONEPKG = new BinaryType("ONEPKG", "4D 53 43 46", "OneNote Package file");
        public static final BinaryType OPT = new BinaryType("OPT", "DB", "Developer Studio File Workspace Options file");
        public static final BinaryType OPT_0 = new BinaryType("OPT", 512, "FD FF FF FF 20 00 00 00", "Developer Studio File Workspace Options subheader (MS Office)");
        public static final BinaryType ORG_PFC = new BinaryType("ORG,PFC", "41 4F 4C 56 4D 31 30 30", "AOL personal file cabinet (PFC) file NOTE: See PFC-Details.zip for PFC file format information.");
        public static final BinaryType OST = new BinaryType("OST", "21 42 44 4E", "Microsoft Outlook Offline Storage Folder File");
        public static final BinaryType OTF = new BinaryType("OTF", "4F 54 54 4F 00", "OpenType font file");
        public static final BinaryType OXPS = new BinaryType("OXPS", "50 4B 03 04", "Microsoft Open XML paper specification file");
        public static final BinaryType P10 = new BinaryType("P10", "64 00 00 00", "Intel PROset/Wireless Profile");
        public static final BinaryType PAB = new BinaryType("PAB", "21 42 44 4E", "Microsoft Outlook Personal Address Book File");
        public static final BinaryType PAK = new BinaryType("PAK", "1A 0B", "Compressed archive file (often associated with Quake Engine games)");
        public static final BinaryType PAK_0 = new BinaryType("PAK", "50 41 43 4B", "Quake archive file");
        public static final BinaryType PAT = new BinaryType("PAT", "47 46 31 50 41 54 43 48", "Advanced Gravis Ultrasound patch file");
        public static final BinaryType PAT_0 = new BinaryType("PAT", "47 50 41 54", "GIMP (GNU Image Manipulation Program) pattern file");
        public static final BinaryType PAX = new BinaryType("PAX", "50 41 58", "PAX password protected bitmap");
        public static final BinaryType PCH = new BinaryType("PCH", "56 43 50 43 48 30", "Visual C PreCompiled header file");
        public static final BinaryType PCS = new BinaryType("PCS", "32 03 10 00 00 00 00 00 00 00 80 00 00 00 FF 00", "Pfaff Home Embroidery file");
        public static final BinaryType PCS_0 = new BinaryType("PCS", "4D 54 68 64", "Yamaha Piano sound file");
        public static final BinaryType PDB = new BinaryType("PDB", 11, "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00", "Palmpilot Database/Document File");
        public static final BinaryType PDB_0 = new BinaryType("PDB", "4D 2D 57 20 50 6F 63 6B 65 74 20 44 69 63 74 69", "Merriam-Webster Pocket Dictionary file");
        public static final BinaryType PDB_1 = new BinaryType("PDB", "4D 69 63 72 6F 73 6F 66 74 20 43 2F 43 2B 2B 20", "Microsoft C++ debugging symbols file");
        public static final BinaryType PDB_2 = new BinaryType("PDB", "73 6D 5F", "PalmOS SuperMemo file");
        public static final BinaryType PDB_3 = new BinaryType("PDB", "73 7A 65 7A", "PowerBASIC Debugger Symbols file");
        public static final BinaryType PDB_4 = new BinaryType("PDB", "AC ED 00 05 73 72 00 12 62 67 62 6C 69 74 7A 2E", "BGBlitz (professional Backgammon software) position database file");
        public static final BinaryType PDF_FDF_AI = new BinaryType("PDF,FDF,AI", "25 50 44 46", "Adobe Portable Document Format, Forms Document Format, and Illustrator graphics files Trailers: 0A 25 25 45 4F 46 (.%%EOF) 0A 25 25 45 4F 46 0A (.%%EOF.) 0D 0A 25 25 45 4F 46 0D 0A (..%%EOF..) 0D 25 25 45 4F 46 0D (.%%EOF.) NOTE: There may be multiple end-of-file marks within the file. When carving, be sure to get the last one.");
        public static final BinaryType PEC = new BinaryType("PEC", "23 50 45 43 30 30 30 31 4C 41 3A", "Brother/Babylock/Bernina Home Embroidery file");
        public static final BinaryType PES = new BinaryType("PES", "23 50 45 53 30", "Brother/Babylock/Bernina Home Embroidery file");
        public static final BinaryType PF = new BinaryType("PF", 4, "53 43 43 41", "Windows prefetch file. The first four bytes indicate the OS: 0x11-00-00-00 = XP, 0x17-00-00-00 = Vista/Win7, and 0x1A-00-00-00 = Win8.1 (and probably Win8, as well).");
        public static final BinaryType PGD = new BinaryType("PGD", "50 47 50 64 4D 41 49 4E", "PGP disk image");
        public static final BinaryType PGM = new BinaryType("PGM", "50 35 0A", "Portable Graymap Graphic");
        public static final BinaryType PIC = new BinaryType("PIC", "00", "IBM Storyboard bitmap file");
        public static final BinaryType PIC_0 = new BinaryType("PIC", "01 00 00 00 01", "Unknown type picture file");
        public static final BinaryType PIF = new BinaryType("PIF", "00", "Windows Program Information File");
        public static final BinaryType PKR = new BinaryType("PKR", "99 01", "PGP public keyring file");
        public static final BinaryType PLIST = new BinaryType("plist", "62 70 6C 69 73 74", "Binary property list (plist) (NOTE: Next two bytes are the version number, currently 0x30-30, or \"00\")");
        public static final BinaryType PLS = new BinaryType("PLS", "5B 70 6C 61 79 6C 69 73 74 5D", "WinAmp Playlist file");
        public static final BinaryType PNG = new BinaryType("PNG", "89 50 4E 47 0D 0A 1A 0A", "Portable Network Graphics file Trailer: 49 45 4E 44 AE 42 60 82 (IEND�B`�...)");
        public static final BinaryType PPT = new BinaryType("PPT", 512, "00 6E 1E F0", "PowerPoint presentation subheader (MS Office)");
        public static final BinaryType PPT_0 = new BinaryType("PPT", 512, "0F 00 E8 03", "PowerPoint presentation subheader (MS Office)");
        public static final BinaryType PPT_1 = new BinaryType("PPT", 512, "A0 46 1D F0", "PowerPoint presentation subheader (MS Office)");
        public static final BinaryType PPZ = new BinaryType("PPZ", "4D 53 43 46", "Powerpoint Packaged Presentation");
        public static final BinaryType PRC = new BinaryType("PRC", "42 4F 4F 4B 4D 4F 42 49", "Palmpilot resource file");
        public static final BinaryType PRC_0 = new BinaryType("PRC", 60, "74 42 4D 50 4B 6E 57 72", "PathWay Map file, used with GPS devices");
        public static final BinaryType PSD = new BinaryType("PSD", "38 42 50 53", "Photoshop image file");
        public static final BinaryType PSP = new BinaryType("PSP", "7E 42 4B 00", "Corel Paint Shop Pro image file");
        public static final BinaryType PST = new BinaryType("PST", "21 42 44 4E", "Microsoft Outlook Personal Folder File");
        public static final BinaryType PUB = new BinaryType("PUB", "DB", "MS Publisher file");
        public static final BinaryType PUB_0 = new BinaryType("PUB", 512, "FD FF FF FF 02", "MS Publisher file subheader");
        public static final BinaryType PUF = new BinaryType("PUF", "50 55 46 58", "Puffer encrypted archive");
        public static final BinaryType PWI = new BinaryType("PWI", "7B 5C 70 77 69", "Microsoft Windows Mobile personal note file");
        public static final BinaryType PWL = new BinaryType("PWL", "B0 4D 46 43", "Windows 95 password file");
        public static final BinaryType PWL_0 = new BinaryType("PWL", "E3 82 85 96", "Windows 98 password file");
        public static final BinaryType QBB = new BinaryType("QBB", "45 86 00 00 06 00", "Intuit QuickBooks backup file");
        public static final BinaryType QBM = new BinaryType("QBM", "DB", "QuickBooks Portable Company File");
        public static final BinaryType QBM_0 = new BinaryType("QBM", 512, "FD FF FF FF 04", "QuickBooks Portable Company File");
        public static final BinaryType QDF = new BinaryType("QDF", "AC 9E BD 8F 00 00", "Quicken data file");
        public static final BinaryType QEL = new BinaryType("QEL", 92, "51 45 4C 20", "Quicken data file");
        public static final BinaryType QPH = new BinaryType("QPH", "03 00 00 00", "Quicken price history file");
        public static final BinaryType QRP = new BinaryType("QRP", "FF 0A 00", "QuickReport Report file");
        public static final BinaryType QXD = new BinaryType("QXD", "00 00 4D 4D 58 50 52", "Quark Express document (Intel & Motorola, respectively) NOTE: It appears that the byte following the 0x52 (\"R\") is the language indicator; 0x33 (\"3\") seems to indicate English and 0x61 (\"a\") reportedly indicates Korean.");
        public static final BinaryType RA = new BinaryType("RA", "2E 52 4D 46 00 00 00 12 00", "RealAudio file");
        public static final BinaryType RAM = new BinaryType("RAM", "72 74 73 70 3A 2F 2F", "RealMedia metafile");
        public static final BinaryType RAR = new BinaryType("RAR", "52 61 72 21 1A 07 00", "RAR (v4.x) compressed archive file");
        public static final BinaryType RAR_0 = new BinaryType("RAR", "52 61 72 21 1A 07 01 00", "RAR (v5) compressed archive file");
        public static final BinaryType RA_0 = new BinaryType("RA", "2E 72 61 FD 00", "RealAudio streaming media file");
        public static final BinaryType RDATA = new BinaryType("RDATA", "52 44 58 32 0A", "R (programming language) saved work space");
        public static final BinaryType REG = new BinaryType("REG", "FF FE", "Windows Registry file");
        public static final BinaryType REG_SUD = new BinaryType("REG,SUD", "52 45 47 45 44 49 54", "Windows NT Registry and Registry Undo files");
        public static final BinaryType RGB = new BinaryType("RGB", "01 DA 01 01 00 03", "Silicon Graphics RGB Bitmap");
        public static final BinaryType RM_RMVB = new BinaryType("RM,RMVB", "2E 52 4D 46", "RealMedia streaming media file");
        public static final BinaryType RPM = new BinaryType("RPM", "ED AB EE DB", "RedHat Package Manager file");
        public static final BinaryType RTD = new BinaryType("RTD", "43 23 2B 44 A4 43 4D A5 48 64 72", "RagTime document file");
        public static final BinaryType RTF = new BinaryType("RTF", "7B 5C 72 74 66", "Rich text format word processing file Trailer: 7D (})");
        public static final BinaryType RVT = new BinaryType("RVT", 512, "00 00 00 00 00 00 00 00", "Revit Project File subheader");
        public static final BinaryType RVT_0 = new BinaryType("RVT", "DB", "Revit Project file");
        public static final BinaryType SAV = new BinaryType("SAV", "24 46 4C 32 40 28 23 29 20 53 50 53 53 20 44 41 54 41 20 46 49 4C 45", "SPSS Statistics (n�e Statistical Package for the Social Sciences, then Statistical Product and Service Solutions) data file");
        public static final BinaryType SBV = new BinaryType("SBV", "46 45 44 46", "(Unknown file type)");
        public static final BinaryType SCT = new BinaryType("SCT", "57 04 00 00 53 50 53 53 20 74 65 6D 70 6C 61 74", "SPSS Statistics (n�e Statistical Package for the Social Sciences, then Statistical Product and Service Solutions) template file");
        public static final BinaryType SDR = new BinaryType("SDR", "53 4D 41 52 54 44 52 57", "SmartDraw Drawing file");
        public static final BinaryType SEA = new BinaryType("SEA", "00", "Mac Stuffit Self-Extracting Archive");
        public static final BinaryType SH3 = new BinaryType("SH3", "48 48 47 42 31", "Harvard Graphics presentation file");
        public static final BinaryType SHD = new BinaryType("SHD", "4B 49 00 00", "Windows 9x printer spool file");
        public static final BinaryType SHD_0 = new BinaryType("SHD", "66 49 00 00", "Windows NT printer spool file");
        public static final BinaryType SHD_1 = new BinaryType("SHD", "67 49 00 00", "Windows 2000/XP printer spool file");
        public static final BinaryType SHD_2 = new BinaryType("SHD", "68 49 00 00", "Windows Server 2003 printer spool file");
        public static final BinaryType SHW = new BinaryType("SHW", "53 48 4F 57", "Harvard Graphics DOS Ver. 2/x Presentation file");
        public static final BinaryType SIB = new BinaryType("SIB", "0F 53 49 42 45 4C 49 55 53", "Sibelius Music - Score file");
        public static final BinaryType SIL = new BinaryType("SIL", "23 21 53 49 4C 4B 0A", "Audio compression format developed by Skype; also used by other applications.");
        public static final BinaryType SIT = new BinaryType("SIT", "53 49 54 21 00", "StuffIt compressed archive");
        public static final BinaryType SIT_0 = new BinaryType("SIT", "53 74 75 66 66 49 74 20 28 63 29 31 39 39 37 2D", "StuffIt compressed archive");
        public static final BinaryType SKF = new BinaryType("SKF", "07 53 4B 46", "SkinCrafter skin file");
        public static final BinaryType SKR = new BinaryType("SKR", "95 01", "PGP secret keyring file");
        public static final BinaryType SLE = new BinaryType("SLE", "3A 56 45 52 53 49 4F 4E", "Surfplan kite project file");
        public static final BinaryType SLE_0 = new BinaryType("SLE", "41 43 76", "Steganos Security Suite virtual secure drive");
        public static final BinaryType SLN = new BinaryType("SLN", "4D 69 63 72 6F 73 6F 66 74 20 56 69 73 75 61 6C 20 53 74 75 64 69 6F 20 53 6F 6C 75 74 69 6F 6E 20 46 69 6C 65", "Visual Studio .NET Solution file");
        public static final BinaryType SNM = new BinaryType("SNM", "00 1E 84 90 00 00 00 00", "Netscape Communicator (v4) mail folder");
        public static final BinaryType SNP = new BinaryType("SNP", "4D 53 43 46", "Microsoft Access Snapshot Viewer file");
        public static final BinaryType SOL = new BinaryType("SOL", "00 BF", "Adobe Flash shared object file (e.g., Flash cookies)");
        public static final BinaryType SOU = new BinaryType("SOU", "DB", "Visual Studio Solution User Options file");
        public static final BinaryType SPF = new BinaryType("SPF", "53 50 46 49 00", "StorageCraft ShadownProtect backup file");
        public static final BinaryType SPL = new BinaryType("SPL", "00 00 01 00", "Windows NT/2000/XP printer spool file");
        public static final BinaryType SPO = new BinaryType("SPO", "DB", "SPSS output file");
        public static final BinaryType SPVCHAIN = new BinaryType("SPVCHAIN", "53 50 56 42", "MultiBit Bitcoin blockchain file");
        public static final BinaryType STL = new BinaryType("STL", "73 6F 6C 69 64", "ASCII STL (STereoLithography) file for 3D printing.");
        public static final BinaryType SUO = new BinaryType("SUO", 512, "FD FF FF FF 04", "Visual Studio Solution User Options subheader (MS Office)");
        public static final BinaryType SWF = new BinaryType("SWF", "43 57 53", "Macromedia Shockwave Flash player file (zlib compressed, SWF 6 and later). See SWF File Format Specification.");
        public static final BinaryType SWF_0 = new BinaryType("SWF", "46 57 53", "Macromedia Shockwave Flash player file (uncompressed). See SWF File Format Specification.");
        public static final BinaryType SWF_1 = new BinaryType("SWF", "5A 57 53", "Macromedia Shockwave Flash player file (LZMA compressed, SWF 13 and later). See SWF File Format Specification.");
        public static final BinaryType SXC = new BinaryType("SXC", "50 4B 03 04", "StarOffice spreadsheet");
        public static final BinaryType SXC_SXD_SXI_SXW = new BinaryType("SXC,SXD,SXI,SXW", "50 4B 03 04", "OpenOffice spreadsheet (Calc), drawing (Draw), presentation (Impress), and word processing (Writer) files, respectively.");
        public static final BinaryType SYM = new BinaryType("SYM", "53 6D 62 6C", "(Unconfirmed file type. Likely type is Harvard Graphics Version 2.x graphic symbol or Windows SDK graphic symbol)");
        public static final BinaryType SYS = new BinaryType("SYS", "FF", "Windows executable (SYS) file");
        public static final BinaryType SYS_0 = new BinaryType("SYS", "FF 4B 45 59 42 20 20 20", "Keyboard driver file");
        public static final BinaryType SYS_1 = new BinaryType("SYS", "FF FF FF FF", "DOS system driver");
        public static final BinaryType SYW = new BinaryType("SYW", "41 4D 59 4F", "Harvard Graphics symbol graphic");
        public static final BinaryType TAR = new BinaryType("TAR", 257, "75 73 74 61 72", "Tape Archive file (http://www.mkssoftware.com/docs/man4/tar.4.asp)");
        public static final BinaryType TAR_Z = new BinaryType("TAR_Z", "1F 9D", "Compressed tape archive file using standard (Lempel-Ziv-Welch) compression");
        public static final BinaryType TAR_Z_0 = new BinaryType("TAR_Z", "1F A0", "Compressed tape archive file using LZH (Lempel-Ziv-Huffman) compression");
        public static final BinaryType TBI = new BinaryType("TBI", "00 00 00 00 14 00 00 00", "Windows Disk Image file");
        public static final BinaryType TBI_0 = new BinaryType("TBI", "01 01 47 19 A4 00 00 00 00 00 00 00", "The Bat! secure e-mail Message Base Index file");
        public static final BinaryType THP = new BinaryType("THP", "54 48 50 00", "Wii/GameCube video file");
        public static final BinaryType TIB = new BinaryType("TIB", "B4 6E 68 44", "Acronis True Image file (early versions)");
        public static final BinaryType TIB_0 = new BinaryType("TIB", "CE 24 B9 A2 20 00 00 00", "Acronis True Image file (current versions)");
        public static final BinaryType TIF_TIFF = new BinaryType("TIF,TIFF", "49 20 49", "Tagged Image File Format file");
        public static final BinaryType TIF_TIFF_0 = new BinaryType("TIF,TIFF", "49 49 2A 00", "Tagged Image File Format file (little endian, i.e., LSB first in the byte; Intel)");
        public static final BinaryType TIF_TIFF_1 = new BinaryType("TIF,TIFF", "4D 4D 00 2A", "Tagged Image File Format file (big endian, i.e., LSB last in the byte; Motorola)");
        public static final BinaryType TIF_TIFF_2 = new BinaryType("TIF,TIFF", "4D 4D 00 2B", "BigTIFF files; Tagged Image File Format files >4 GB");
        public static final BinaryType TLB = new BinaryType("TLB", "4D 53 46 54 02 00 01 00", "OLE, SPSS, or Visual C++ type library file");
        public static final BinaryType TORRENT = new BinaryType("TORRENT", "64 38 3A 61 6E 6E 6F 75 6E 63 65", "Torrent file");
        public static final BinaryType TPL = new BinaryType("TPL", "00 20 AF 30", "Wii images container");
        public static final BinaryType TPL_0 = new BinaryType("TPL", "6D 73 46 69 6C 74 65 72 4C 69 73 74", "Internet Explorer v11 Tracking Protection List file");
        public static final BinaryType TR1 = new BinaryType("TR1", "01 10", "Novell LANalyzer capture file");
        public static final BinaryType TS_TSA_TSV = new BinaryType("TS,TSA,TSV", "47", "MPEG transport stream file. (This is not a lot to go on, but MPEG-2 Part 1 Transport (MP2T) files are reportedly broken into 188-byte packets and the 0x47 byte is the sync byte, so should repeat every 188 bytes in the file.)");
        public static final BinaryType TTF = new BinaryType("TTF", "00 01 00 00 00", "TrueType font file");
        public static final BinaryType TTF_0 = new BinaryType("TTF", "74 72 75 65 00", "TrueType font file");
        public static final BinaryType UCE = new BinaryType("UCE", "55 43 45 58", "Unicode extensions");
        public static final BinaryType UFA = new BinaryType("UFA", "55 46 41 C6 D2 C1", "UFA compressed archive");
        public static final BinaryType VBE = new BinaryType("VBE", "23 40 7E 5E", "VBScript Encoded script");
        public static final BinaryType VCD = new BinaryType("VCD", "45 4E 54 52 59 56 43 44 02 00 00 01 02 00 18 58", "VideoVCD (GNU VCDImager) file");
        public static final BinaryType VCF = new BinaryType("VCF", "42 45 47 49 4E 3A 56 43 41 52 44 0D 0A", "vCard file");
        public static final BinaryType VCW = new BinaryType("VCW", "5B 4D 53 56 43", "Microsoft Visual C++ Workbench Information File");
        public static final BinaryType VHD = new BinaryType("VHD", "63 6F 6E 65 63 74 69 78", "Virtual PC Virtual HD image");
        public static final BinaryType VLT = new BinaryType("VLT", "1F 8B 08", "VLC Player Skin file");
        public static final BinaryType VMDK = new BinaryType("VMDK", "23 20 44 69 73 6B 20 44 65 73 63 72 69 70 74 6F", "VMware 4 Virtual Disk description file (split disk)");
        public static final BinaryType VMDK_0 = new BinaryType("VMDK", "43 4F 57 44", "VMware 3 Virtual Disk (portion of a split disk) file");
        public static final BinaryType VMDK_1 = new BinaryType("VMDK", "4B 44 4D", "VMware 4 Virtual Disk (portion of a split disk) file");
        public static final BinaryType VMDK_2 = new BinaryType("VMDK", "4B 44 4D 56", "VMware 4 Virtual Disk (monolitic disk) file");
        public static final BinaryType VOC = new BinaryType("VOC", "43 72 65 61 74 69 76 65 20 56 6F 69 63 65 20 46", "Creative Voice audio file");
        public static final BinaryType VSD = new BinaryType("VSD", "DB", "Visio file");
        public static final BinaryType WAB = new BinaryType("WAB", "81 32 84 C1 85 05 D0 11 B2 90 00 AA 00 3C F6 76", "Outlook Express address book (Win95)");
        public static final BinaryType WAB_0 = new BinaryType("WAB", "9C CB CB 8D 13 75 D2 11 91 58 00 C0 4F 79 56 A4", "Outlook address file");
        public static final BinaryType WALLET = new BinaryType("WALLET", "0A 16 6F 72 67 2E 62 69 74 63 6F 69 6E 2E 70 72", "MultiBit Bitcoin wallet file");
        public static final BinaryType WB2 = new BinaryType("WB2", "00 00 02 00", "QuattroPro for Windows Spreadsheet file");
        public static final BinaryType WB3 = new BinaryType("WB3", 24, "3E 00 03 00 FE FF 09 00 06", "Quatro Pro for Windows 7.0 Notebook file");
        public static final BinaryType WEBM = new BinaryType("WEBM", "1A 45 DF A3", "WebM video file");
        public static final BinaryType WIM = new BinaryType("WIM", "4D 53 57 49 4D", "Microsoft Windows Imaging Format file");
        public static final BinaryType WK1 = new BinaryType("WK1", "00 00 02 00 06 04 06 00 08 00 00 00 00 00", "Lotus 1-2-3 spreadsheet (v1) file");
        public static final BinaryType WK3 = new BinaryType("WK3", "00 00 1A 00 00 10 04 00 00 00 00 00", "Lotus 1-2-3 spreadsheet (v3) file");
        public static final BinaryType WK4_WK5 = new BinaryType("WK4,WK5", "00 00 1A 00 02 10 04 00 00 00 00 00", "Lotus 1-2-3 spreadsheet (v4, v5) file");
        public static final BinaryType WKS = new BinaryType("WKS", "0E 57 4B 53", "DeskMate Worksheet");
        public static final BinaryType WKS_0 = new BinaryType("WKS", "FF 00 02 00 04 04 05 54 02 00", "Works for Windows spreadsheet file");
        public static final BinaryType WMF = new BinaryType("WMF", "01 00 09 00 00 03", "Windows Metadata file (Win 3.x format)");
        public static final BinaryType WMF_0 = new BinaryType("WMF", "D7 CD C6 9A", "Windows graphics metafile");
        public static final BinaryType WMZ = new BinaryType("WMZ", "50 4B 03 04", "Windows Media compressed skin file");
        public static final BinaryType WPF = new BinaryType("WPF", "81 CD AB", "WordPerfect text file");
        public static final BinaryType WPL = new BinaryType("WPL", 84, "4D 69 63 72 6F 73 6F 66 74 20 57 69 6E 64 6F 77 73 20 4D 65 64 69 61 20 50 6C 61 79 65 72 20 2D 2D 20", "Windows Media Player playlist");
        public static final BinaryType WPS = new BinaryType("WPS", "DB", "MSWorks text document");
        public static final BinaryType WP_WPD_WPG_WPP_WP5_WP6 = new BinaryType("WP,WPD,WPG,WPP,WP5,WP6", "FF 57 50 43", "WordPerfect text and graphics file");
        public static final BinaryType WRI = new BinaryType("WRI", "32 BE", "Microsoft Write file");
        public static final BinaryType WRI_0 = new BinaryType("WRI", "BE 00 00 00 AB 00 00 00 00 00 00 00 00", "MS Write file");
        public static final BinaryType WS = new BinaryType("WS", "1D 7D", "WordStar Version 5.0/6.0 document");
        public static final BinaryType WS2 = new BinaryType("WS2", "57 53 32 30 30 30", "WordStar for Windows Ver. 2 document");
        public static final BinaryType WSC = new BinaryType("WSC", "3C 3F", "Windows Script Component");
        public static final BinaryType WSC_0 = new BinaryType("WSC", "EF BB BF 3C 3F", "Windows Script Component (UTF-8)");
        public static final BinaryType WSF = new BinaryType("WSF", "3C", "Windows Script Component");
        public static final BinaryType WSF_0 = new BinaryType("WSF", "EF BB BF 3C", "Windows Script Component (UTF-8)");
        public static final BinaryType XAR = new BinaryType("XAR", "78 61 72 21", "eXtensible ARchive file");
        public static final BinaryType XCF = new BinaryType("XCF", "67 69 6d 70 20 78 63 66 20", "GNU Image Manipulation Program (GIMP) eXperimental Computing Facility (XCF) image file");
        public static final BinaryType XDR = new BinaryType("XDR", "3C", "BizTalk XML-Data Reduced Schema file");
        public static final BinaryType XLS = new BinaryType("XLS", 512, "09 08 10 00 00 06 05 00", "Excel spreadsheet subheader (MS Office)");
        public static final BinaryType XLS_0 = new BinaryType("XLS", 512, "FD FF FF FF 20 00 00 00", "Excel spreadsheet subheader (MS Office)");
        public static final BinaryType XPI = new BinaryType("XPI", "50 4B 03 04", "Mozilla Browser Archive");
        public static final BinaryType XPS = new BinaryType("XPS", "50 4B 03 04", "XML paper specification file");
        public static final BinaryType XPT = new BinaryType("XPT", "48 45 41 44 45 52 20 52 45 43 4F 52 44 2A 2A 2A", "SAS Transport dataset file format");
        public static final BinaryType XPT_0 = new BinaryType("XPT", "50 4B 03 04", "eXact Packager Models");
        public static final BinaryType XPT_1 = new BinaryType("XPT", "58 50 43 4F 4D 0A 54 79 70 65 4C 69 62", "XPCOM type libraries for the XPIDL compiler");
        public static final BinaryType XUL = new BinaryType("XUL", "3C 3F 78 6D 6C 20 76 65 72 73 69 6F 6E 3D 22 31 2E 30 22 3F 3E", "XML User Interface Language file");
        public static final BinaryType XXX = new BinaryType("XXX", "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00", "Compucon/Singer embroidery design file");
        public static final BinaryType XZ = new BinaryType("XZ", "FD 37 7A 58 5A 00", "XZ archive file");
        public static final BinaryType YTR = new BinaryType("YTR", "00", "IRIS OCR data file");
        public static final BinaryType ZAP = new BinaryType("ZAP", "4D 5A 90 00 03 00 00 00 04 00 00 00 FF FF", "ZoneAlam data file");
        public static final BinaryType ZIP = new BinaryType("ZIP", "50 4B 03 04", "PKZIP archive file (Ref. 1 | Ref. 2) Trailer: filename 50 4B 17 characters 00 00 00 Trailer: (filename PK 17 characters ...) Note: PK are the initals of Phil Katz, co-creator of the ZIP file format and author of PKZIP.");
        public static final BinaryType ZIP_0 = new BinaryType("ZIP", "50 4B 03 04", "Apple Mac OS X Dashboard Widget, Aston Shell theme, Oolite eXpansion Pack, Opera Widget, Pivot Style Template, Rockbox Theme package, Simple Machines Forums theme, SubEthaEdit Mode, Trillian zipped skin, Virtual Skipper skin");
        public static final BinaryType ZIP_1 = new BinaryType("ZIP", "50 4B 03 04 14 00 01 00 63 00 00 00 00 00", "ZLock Pro encrypted ZIP");
        public static final BinaryType ZIP_2 = new BinaryType("ZIP", "50 4B 05 06", "PKZIP empty archive file");
        public static final BinaryType ZIP_3 = new BinaryType("ZIP", "50 4B 07 08", "PKZIP multivolume archive file");
        public static final BinaryType ZIP_4 = new BinaryType("ZIP", 30, "50 4B 4C 49 54 45", "PKLITE compressed ZIP archive (see also PKZIP)");
        public static final BinaryType ZIP_5 = new BinaryType("ZIP", 526, "50 4B 53 70 58", "PKSFX self-extracting executable compressed file (see also PKZIP)");
        public static final BinaryType ZIP_6 = new BinaryType("ZIP", 29152, "57 69 6E 5A 69 70", "WinZip compressed archive");
        public static final BinaryType ZOO = new BinaryType("ZOO", "5A 4F 4F 20", "ZOO compressed archive");
        public static final BinaryType _123 = new BinaryType("123", "00 00 1A 00 05 10 04", "Lotus 1-2-3 spreadsheet (v9) file");
        public static final BinaryType _3GG_3GP_3G2 = new BinaryType("3GG,3GP,3G2", 4, "66 74 79 70 33 67 70", "3rd Generation Partnership Project 3GPP multimedia files");
        public static final BinaryType _4XM = new BinaryType("4XM", "52 49 46 46", "4X Movie video");
        public static final BinaryType _7Z = new BinaryType("7Z", "37 7A BC AF 27 1C", "7-Zip compressed file");
    }

    static {
        Multimap<String, BinaryType> definedTypes = LinkedHashMultimap.create();
        Field[] fields = PreDefines.class.getFields();
        for (Field field : fields) {
            if (!BinaryType.class.equals(field.getType())) {
                continue;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                try {
                    BinaryType value = (BinaryType) field.get(null);
                    for (String ext : value.extensions) {
                        definedTypes.put(ext.toLowerCase(), value);
                    }
                } catch (IllegalAccessException e) {
                    //ignore
                }
            }
        }
        DEFINED_TYPES = definedTypes;
    }

    private BinaryType() {
        this.magicNumberOffset = -1;
        this.extensionsText = null;
        this.extensions = null;
        this.description = null;
    }

    public BinaryType(String extensionsText, String hexMarks, String description) {
        this(extensionsText, 0, hexMarks, description);
    }

    public BinaryType(String extensionsText, int magicNumberOffset, String hexMarks, String description) {
        super(hexMarks.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll(" ", ""));
        if (magicNumberOffset < 0) {
            throw new IllegalArgumentException();
        }
        this.magicNumberOffset = magicNumberOffset;
        this.extensionsText = extensionsText;
        this.extensions = this.extensionsText.split("\\,");
        if (this.extensions.length <= 0) {
            throw new IllegalArgumentException();
        }
        this.description = description;
    }

    @Override
    protected boolean match(File file) {
        if (magicNumberOffset >= 0) {
            return super.match(file, magicNumberOffset);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (StringUtils.isNotEmpty(extensionsText)) {
            if (StringUtils.isEmpty(description)) {
                return "*." + extensionsText;
            } else {
                return "*." + extensionsText + ": " + description;
            }
        } else {
            if (StringUtils.isEmpty(description)) {
                return "UNKNOWN";
            } else {
                return "()" + extensionsText + ": " + description;
            }
        }
    }

    public static Optional<BinaryType> checkType(File file) {
        final String ext = FilenameUtils.getExtension(file.getName().toLowerCase());
        for (BinaryType type : DEFINED_TYPES.get(ext)) {
            if (type.match(file)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
