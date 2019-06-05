package com.cicadat.example;

public class AppConsumer {


    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        new OrderConsumer().orderCancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }
}
