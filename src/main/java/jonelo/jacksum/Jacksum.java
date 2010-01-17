/******************************************************************************
 *
 * Jacksum version 1.1.1 - checksum utility in Java
 * Copyright (C) 2001, 2002 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, 
 * All Rights Reserved, http://www.jonelo.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: jonelo@jonelo.de
 *
 *****************************************************************************/

package jonelo.jacksum;
import jonelo.jacksum.algorithm.*;
import java.io.*;
import java.util.*;
                      
public class Jacksum {

   private AbstractChecksum checksum = null;
   private boolean _f=false;

   
   public static void main(String args[])  {   
     new Jacksum(args);   
   }
   
  public void gpl()
  {
    System.out.println ("\n Jacksum v1.1.1, Copyright (C) 2002, Dipl.-Inf. (FH) Johann N. Loefflmann\n");
    System.out.println (" Jacksum comes with ABSOLUTELY NO WARRANTY; for details see 'license.txt'.");
    System.out.println (" This is free software, and you are welcome to redistribute it under certain");
    System.out.println (" conditions; see 'license.txt' for details.");
    System.out.println (" Visit http://www.jonelo.de/java/jacksum/index.html for the latest version.\n");
  }
  
  public void help_short()
  {
    gpl();
    System.out.println (" For more information please type:");
    System.out.println (" java Jacksum -h en");
    System.out.println ("\n Fuer weitere Informationen bitte eingeben:");
    System.out.println (" java Jacksum -h de\n");
    System.exit(0);
  }
  
  public void help_long(String filename)
  {
     try {
       showHelp(filename);
     } catch (Exception e) { 
       System.err.println("Problem while reading helpfile "+filename); 
     }
     System.exit(0);
  }
   
  private void showHelp(String filename) throws Exception {
      InputStream is = getClass().getResourceAsStream(filename);
      BufferedReader br = new BufferedReader (new InputStreamReader(is));
      String thisLine;
      while ((thisLine = br.readLine()) != null)
      {
         System.out.println(thisLine);       
      }
  }   

                    
   public void recursDir(String dirItem) 
   {    
      String list[];
      File file = new File(dirItem);
      if (file.isDirectory()) {
          System.out.println("\n"+file+":");
          Vector<Object> vd = new Vector<Object>();
          Vector<Object> vf = new Vector<Object>();
          list = file.list();
          Arrays.sort(list,String.CASE_INSENSITIVE_ORDER);          
          
          for (int i = 0; i < list.length; i++)
          {
              // vorsortieren nach files und dirs
             File f = new File(dirItem + File.separatorChar + list[i]);
             if (f.isDirectory()) vd.add(list[i]); else vf.add(list[i]);
          }
                    
          // little header
          String tmp = vf.size()+" file";
          if (vf.size() != 1) tmp=tmp+"s";
          if (!_f)
          {
            tmp=tmp+", "+vd.size()+" director";
            if (vd.size() != 1) tmp=tmp+"ies"; else tmp=tmp+"y";
          }
          System.out.println(tmp);
          
          // files...
          for (int a=0; a < vf.size(); a++) 
            recursDir (dirItem + File.separatorChar + vf.get(a));
            
          // dirs...  
          if (!_f)
          {
            for (int c=0; c < vd.size(); c++)           
              System.out.println("Jacksum: "+vd.get(c)+": Is a directory");             
          }
          for (int d=0; d < vd.size(); d++)
            recursDir (dirItem + File.separatorChar + vd.get(d));
      } else
      processItem(dirItem);
    }   
    
    public void oneDir(String dirItem)
    {
      String list[];
      File file = new File(dirItem);
     
      if (file.isDirectory()) {
          list = file.list();
          Arrays.sort(list,String.CASE_INSENSITIVE_ORDER);
          
          for (int i = 0; i < list.length; i++)
          {
             File f = new File(dirItem + File.separatorChar + list[i]);
             if (f.isDirectory()) {     
                if (!_f) System.out.println("Jacksum: "+list[i]+": Is a directory");
             } else
             processItem(dirItem + File.separatorChar + list[i]);
          }          
      } else
      processItem(dirItem);
    }
    
    public void processItem(String dirItem)
    {
      try {
        checksum.readFile(dirItem);
        File f = new File(dirItem);
        checksum.setFilename(f.getName());
        String ret = checksum.toString();
        if (ret != null) System.out.println(ret);      
      } catch (Exception e) { System.out.println(e); }        
    }
    
    
   public Jacksum(String args[])
   { 
      jonelo.sugar.util.GeneralProgram.requiresMinimumJavaVersion("1.3.0");
      boolean stdin=false, _r=false, _s=false, _d=false, _x=false, _X=false;
      String arg = null;
      String separator=null;      
      int firstfile = 0;
      
      if (args.length == 0) help_short(); else
      if (args.length > 0) {
        checksum = new Crc32(); // default      
        while (firstfile < args.length && args[firstfile].startsWith("-"))
        {
          arg = args[firstfile++];
          if (arg.equals("-a"))
          {              
              if (firstfile < args.length)
              {
                  arg=args[firstfile++].toLowerCase();
                  if (arg.equals("crc32")) {       
                     checksum = new Crc32();
                  } else if (arg.equals("crc16")) {
                     checksum = new Crc16();
                  } else if (arg.equals("adler32")) {
                     checksum = new Adler32();
                  } else if (arg.equals("bsd")) {
                     checksum = new Sum();
                  } else if (arg.equals("sysv")) {
                     checksum = new SumSysV();
                  } else if (arg.equals("cksum")) {
                     checksum = new Cksum();
                  } else if (arg.equals("md2") || arg.equals("md2sum")) {
                     checksum = new MD("MD2");                 
                  } else if (arg.equals("md5") || arg.equals("md5sum")) {
                    checksum = new MD("MD5");
                  } else if (arg.equals("sha") || arg.equals("sha1")) { 
                     checksum = new MD("SHA"); 
                  } else
                     checksum = null; // unknown
              } else
              {
                  System.err.println("-a requires an algorithm. Use -h for help. Exit.");
                  System.exit(2);
              }                       
          } else if (arg.equals("-s")) {
              _s=true;
              if (firstfile < args.length)
              {
                  arg=args[firstfile++];
                  separator=jonelo.sugar.util.GeneralString.translateEscapeSequences(arg);                  
              } else
              {
                  System.err.println("-s requires a separator string. Use -h for help. Exit.");
                  System.exit(2);
              }              
          } else if (arg.equals("-f")) {
             _f = true;
          } else if (arg.equals("-")) {
             stdin = true;
          } else if (arg.equals("-r")) {
             _r = true;
          } else if (arg.equals("-x")) {
             _x = true;
          } else if (arg.equals("-X")) {
             _X = true;
          } else if (arg.equals("-h")) {
              String code=null;
              if (firstfile < args.length)
              {
                  code=args[firstfile++].toLowerCase();
              } else
              {
                  code="en";
              }
              help_long("/help/help_"+code+".txt");              
          } else           
          {
             System.out.println("Unknown argument. Use -h for help. Exit.");
             System.exit(1);
          }
        } // end while                      
      }
      if (checksum==null)
      {
         System.out.println("Unknown algorithm. Use -a <code> to specify one.\nFor help and a list of all supported algorithms use -h. Exit.");
         System.exit(2);
      }
      if (_s) checksum.setSeparator(separator);
      checksum.setHex(_x);      
      if (_X) { 
        checksum.setHex(true);
        checksum.setUpperCase(true); 
      }

      String ret=null;
      String filename=null;      

      if (args.length-firstfile==1) // only 1 parameter
      {
          String dir=args[firstfile];          
          // check if it is a directory
          File f = new File(dir);
          if (!f.exists())
          {
             System.out.println("Jacksum: "+dir+": No such file or directory. Exit.");
             System.exit(3);
          }
          else
          {
             if (f.isDirectory()) _d=true;            
          }          
      }
      
      
      if (_r || _d)
      {
          String dir=null;
          if (args.length-firstfile==1) dir=args[firstfile]; else
          if (args.length == firstfile) dir="."; else
          {
              System.out.println("Too many parameters. One directory was expeced. Exit.");
              System.exit(3);              
          }
          File f = new File(dir);
          if (!f.exists())
          {
             System.out.println("Jacksum: "+dir+": No such file or directory. Exit.");
             System.exit(3);
          }
          else
          {
            if (f.isDirectory())
            {
               if (_r) recursDir(dir);
               else oneDir(dir);
            } else
            {
                System.out.println("Parameter is a file, but a directory was expected. Exit.");
                System.exit(3);
            }
          }
      } else
          
      
      // processing standard input
      if (stdin || (firstfile==args.length)) // no file parameter
      {
        checksum.setFilename("");
        String s=null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
          do {
               s=in.readLine();                 
               if (s!=null)
               {
                   // better than s=s+"\n";                   
                   StringBuffer sb=new StringBuffer(s.length()+1);
                   sb.insert(0,s);
                   sb.insert(s.length(),'\n');
                   checksum.update(sb.toString().getBytes());
               }
          } while (s!=null);
          System.out.println(checksum.toString());
        } catch (Exception e) {e.printStackTrace();}
      } else      
          
      // processing arguments file list
      for (int i=firstfile; i < args.length; i++)
      {
         filename=args[i];
         try {
           File file = new File(filename);
           ret=null;
           if (!file.exists())
           {
               ret = "Jacksum: "+filename+": No such file or directory";
           } else                 
           {
             if (file.isDirectory()) // directory                 
             {            
                if (!_f) ret="Jacksum: "+filename+": Is a directory";                
             } else // file
             {
                processItem(filename);
             }
           }          
           if (ret != null) System.out.println(ret);             
                 
          } catch (Exception e)
          {
            System.err.println(e);
          }
       } 
    }
}
