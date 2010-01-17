
    Corruption Corrector


 ### Introduction ###

Corruption Corrector (CCorr) is a program designed for fixing corrupt 
files. It is for situations when you have (downloaded) many copies of 
the same file, but all of the copies are a bit corrupt. If the 
corruptions are in different parts of the file, it is possible to 
combine the good bytes from each file and get an uncorrupted copy of 
the file.

At first CCorr creates checksums from the files at regular intervals 
(e.g. 16 KB) after which the files can be compared to locate the 
corrupted parts. The user chooses which parts are corrupt and which 
are good, after which CCorr writes a combination of all the good parts 
into a new file, and thus fixes the corruptions if possible.

This program has been made by Esko Luontola using the Java programming 
language. Corruption Corrector is licenced under the GNU General 
Public License (GPL).

Web site: http://ccorr.sourceforge.net/


 ### System Requirements ###

- Java 6 JRE or later from http://java.sun.com/
- Enough free hard drive space for the output files


 ### Installing and Running ###

Unpack with WinRAR, Winzip, unzip or a similar program

Download and install (if not already done so) the latest version of 
the Java Runtime Environment (JRE) from http://java.sun.com/

Start the program with "java -jar CCorr.jar" or "javaw -jar CCorr.jar" 
or by double clicking the JAR file


 ### Uninstalling ###

Delete all the files that came with CCorr

Delete the .ccorr.cfg file in your user home directory (e.g. 
"C:\Documents and Settings\Username\" on Windows NT/2000/XP, 
"/home/username/" on Unix/Linux) to remove all saved settings


 ### License ###

Copyright (C) 2003-2006  Esko Luontola, www.orfjackal.net

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
