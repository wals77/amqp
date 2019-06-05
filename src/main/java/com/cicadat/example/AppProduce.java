package com.cicadat.example;

public class AppProduce {


    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++){
            new Thread(){
                @Override
                public void run() {
                    try {
                        Order order = new OrderProducer().createOrder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }
    }
}
