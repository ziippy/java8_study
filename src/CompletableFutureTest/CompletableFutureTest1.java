package CompletableFutureTest;

import java.util.Random;
import java.util.concurrent.*;

public class CompletableFutureTest1 {
    public static void main(String[] args) {
        /**
         * - Future 제한
         * Future는 여러개의 Future가 있을때 이들간의 의존성을 표현하기가 어렵다.
         * Future#1, Future#2, Future#3, Future#4 를 통해 비동기로 수행한 후 다음과 같은 요구사항이 있다고 해보자.
         *
         * "Future#1 의 계산결과를 Future#2로 전달하라. 그리고 Future#2의 계산이 끝나면 Future#3, Future#4의 결과와 조합하라."
         *
         * 자바 8에서 새로 제공하는 CompletableFuture는 이런 기능을 선언형으로 이용할 수 있다.
         * Future와 CompletableFuture는 Collections와 Stream의 관계와 같다고 할 수 있다.
         */

        // 아래에서 만든 Airline 를 이용해서 테스트 해보자.
        Test();
        System.out.println("===========================");
        /** 출력
         * getTicketPriceAsync before : 1648821281607
         * getTicketPriceAsync after : 1648821281607
         * Do Something Else...............
         * Prices is 0.0
         * endTime : 1648821282632
         * process end - elapsed time: 1025 ms
         */
        //---------------------> But, 이건 get() 에서 계속 blocking 이므로.. 빠져나오지 못할 수 도 있다.

        // get() 에 timeout 을 설정한 후 테스트
        TestWithTimeout();
        System.out.println("===========================");
        /** 출력
         * getTicketPriceAsync before : 1648821942691
         * getTicketPriceAsync after : 1648821942691
         * Do Something Else...............
         * get timeout
         * endTime : 1648821942794
         * process end - elapsed time: 103 ms
         */

        // 추가로 CompletableFuture 내부에서 발생한 예외를 사용자에게 전달할 수 도 있다.
        TestWithException();
        System.out.println("===========================");
        /** 출력
         * getTicketPriceAsync before : 1648821631119
         * getTicketPriceAsync after : 1648821631119
         * Do Something Else...............
         * endTime : 1648821631121
         * process end - elapsed time: 2 ms
         * ===========================
         * java.util.concurrent.ExecutionException: java.lang.RuntimeException: from-to place must be not same.
         * 	at java.base/java.util.concurrent.CompletableFuture.reportGet(CompletableFuture.java:396)
         * 	at java.base/java.util.concurrent.CompletableFuture.get(CompletableFuture.java:2073)
         * 	at CompletableFutureTest.CompletableFutureTest1.TestWithException(CompletableFutureTest1.java:82)
         * 	at CompletableFutureTest.CompletableFutureTest1.main(CompletableFutureTest1.java:34)
         * Caused by: java.lang.RuntimeException: from-to place must be not same.
         * 	at CompletableFutureTest.Airline.validateTicket(CompletableFutureTest1.java:168)
         * 	at CompletableFutureTest.Airline.lambda$getTicketPriceAsyncWithException$1(CompletableFutureTest1.java:147)
         * 	at java.base/java.lang.Thread.run(Thread.java:833)
         */
    }

    public static void Test() {
        Airline airline = new Airline();

        long startTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync before : " + startTime);

        Future<Double> futureTicketPrice = airline.getTicketPriceAsync("KOR", "JPN");

        long futureReturnTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync after : " + startTime);

        System.out.println("Do Something Else...............");

        try {
            double ticketPrices = futureTicketPrice.get();
            System.out.println("Prices is " + ticketPrices);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("endTime : " + endTime);

        System.out.println("process end - elapsed time: " + (endTime - startTime) + " ms");
    }

    public static void TestWithTimeout() {
        Airline airline = new Airline();

        long startTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync before : " + startTime);

        Future<Double> futureTicketPrice = airline.getTicketPriceAsync("KOR", "JPN");

        long futureReturnTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync after : " + startTime);

        System.out.println("Do Something Else...............");

        try {
            double ticketPrices = futureTicketPrice.get(100L, TimeUnit.MILLISECONDS);
            System.out.println("Prices is " + ticketPrices);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("get timeout");
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("endTime : " + endTime);

        System.out.println("process end - elapsed time: " + (endTime - startTime) + " ms");
    }

    public static void TestWithException() {
        Airline airline = new Airline();

        long startTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync before : " + startTime);

        Future<Double> futureTicketPrice = airline.getTicketPriceAsyncWithException("KOR", "KOR");

        long futureReturnTime = System.currentTimeMillis();
        System.out.println("getTicketPriceAsync after : " + startTime);

        System.out.println("Do Something Else...............");

        try {
            double ticketPrices = futureTicketPrice.get();
            System.out.println("Prices is " + ticketPrices);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("endTime : " + endTime);

        System.out.println("process end - elapsed time: " + (endTime - startTime) + " ms");
    }
}

// 비동기 API 테스트용 클래스
class Airline {

    /**
     * 비동기 API 생성
     * getTicketPrice 메서드를 비동기 메서드로 전환해보자.
     * getTicketPriceAsync 는 바로 리턴한다.
     * 대신, 비동기 가격을 반환할 CompletableFuture 인스턴스를 생성 후 리턴
     * 실제 가격 계산은 익명 스레드가 수행하고, 끝나면 CompleetableFuture 인스턴스를 이용해서 complete 처리
     */
    public Future<Double> getTicketPriceAsync(String from, String to){
        CompletableFuture<Double> futurePrices = new CompletableFuture<>();

        new Thread(() ->  {
            double price = getTicketPrice(from, to);
            futurePrices.complete(price);
        }).start();

        return futurePrices;
    }

    // 출발지와 목적지를 알려주면 사용자에게 티켓 가격정보를 리턴하는 함수
    public double getTicketPrice(String from, String to) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random random = new Random();

        int fromToLength = 0;
        if (from.isEmpty() || to.isEmpty()) {
            fromToLength = 1;
        }
        else {
            fromToLength = Math.abs(from.length() - to.length());
        }

        return random.nextDouble() * fromToLength;
    }

    //
    public Future<Double> getTicketPriceAsyncWithException(String from, String to){
        CompletableFuture<Double> futurePrices = new CompletableFuture<>();

        new Thread(() ->  {
            try {
                validateTicket(from, to);

                double price = getTicketPrice(from, to);
                futurePrices.complete(price);
            } catch(Exception e) {
                futurePrices.completeExceptionally(e);
            }
        }).start();

        return futurePrices;
    }

    private void validateTicket(String from, String to) {

        if(from == null || from.isEmpty()) {
            throw new RuntimeException("from place is empty");
        }
        else if(to == null || to.isEmpty()) {
            throw new RuntimeException("to place is empty");
        }
        else if(from.equals(to)) {
            throw new RuntimeException("from-to place must be not same.");
        }
    }
}
