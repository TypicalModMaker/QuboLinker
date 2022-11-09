package dev.isnow.qubolinker;

public class QuboLinker {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("START WITH: java -jar Client.jar masterIP vpsName");
            return;
        }
        new QuboClient(args);
    }
}
