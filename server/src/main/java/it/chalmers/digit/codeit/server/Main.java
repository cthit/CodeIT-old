package it.chalmers.digit.codeit.server;

import pong_sample.PongGame;

public class Main {
    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.start();
//        if (args.length == 1){
//            System.out.println("Using " + args[0] + " as source path");
//            ServerController serverController = new ServerController();
//            serverController.start();
//        } else {
//            System.out.println(String.format("Need exactly 1 argument. Got: %d\n" +
//                    "Argument is path to the source.jar file. (The Challenge implemented)", args.length));
//        }
        System.out.println("End of main");
    }
}
