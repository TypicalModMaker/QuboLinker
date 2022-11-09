package dev.isnow.qubolinker.util;

import lombok.experimental.UtilityClass;

import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@UtilityClass
public class FileUtil {

    public void writeSplash(File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
        } catch (FileNotFoundException e) {
            // wtf?
            e.printStackTrace();
            System.exit(69);
        } catch (UnsupportedEncodingException e) {
            // wtf?
            e.printStackTrace();
            System.exit(70);
        }
        writer.write("  /$$$$$$  /$$   /$$ /$$$$$$$   /$$$$$$        /$$       /$$$$$$ /$$   /$$ /$$   /$$ /$$$$$$$$ /$$$$$$$ " + "\n");
        writer.write(" /$$__  $$| $$  | $$| $$__  $$ /$$__  $$      | $$      |_  $$_/| $$$ | $$| $$  /$$/| $$_____/| $$__  $$" + "\n");
        writer.write("| $$  \\ $$| $$  | $$| $$  \\ $$| $$  \\ $$      | $$        | $$  | $$$$| $$| $$ /$$/ | $$      | $$  \\ $$" + "\n");
        writer.write("| $$  | $$| $$  | $$| $$$$$$$ | $$  | $$      | $$        | $$  | $$ $$ $$| $$$$$/  | $$$$$   | $$$$$$$/" + "\n");
        writer.write("| $$  | $$| $$  | $$| $$__  $$| $$  | $$      | $$        | $$  | $$  $$$$| $$  $$  | $$__/   | $$__  $$" + "\n");
        writer.write("| $$/$$ $$| $$  | $$| $$  \\ $$| $$  | $$      | $$        | $$  | $$\\  $$$| $$\\  $$ | $$      | $$  \\ $$" + "\n");
        writer.write("|  $$$$$$/|  $$$$$$/| $$$$$$$/|  $$$$$$/      | $$$$$$$$ /$$$$$$| $$ \\  $$| $$ \\  $$| $$$$$$$$| $$  | $$" + "\n");
        writer.write(" \\____ $$$ \\______/ |_______/  \\______/       |________/|______/|__/  \\__/|__/  \\__/|________/|__/  |__/" + "\n");
        writer.write("      \\__/                                                                                              " + "\n");
        writer.write("Made by Isnow for my love, Spooky." + "\n");
        writer.write("Started at " + new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss").format(new Date(System.currentTimeMillis())) + "\n");
        writer.close();
    }

    public ArrayList<InetAddress> getVPSList() {
        BufferedReader br;
        ArrayList<InetAddress> ips = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader("vpses.txt"));
            try {
                String line = br.readLine();

                while (line != null) {
                    ips.add(InetAddress.getByName(line));
                    System.out.println("GOT IP: " + line);
                    line = br.readLine();
                }

            } finally {
                br.close();
            }
        } catch (Exception e) {
            System.out.println("FAILED TO READ vpses.txt!");
            e.printStackTrace();
            System.exit(0);
        }
        return ips;
    }

    public void writeOutput(String output, File file) {
        try {
            FileWriter fw;
            PrintWriter pw;
            fw = new FileWriter(file, true);
            pw = new PrintWriter(fw);
            pw.write(output + "\n");
            pw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Failed to save output!");
        }
    }
}
