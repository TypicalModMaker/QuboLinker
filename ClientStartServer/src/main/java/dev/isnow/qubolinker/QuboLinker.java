package dev.isnow.qubolinker;

public class QuboLinker {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("START WITH: java -jar ClientStartServer-1.0-SNAPSHOT.jar vpsName");
            return;
        }
        new QuboStartServer(args[0]);
    }
}
