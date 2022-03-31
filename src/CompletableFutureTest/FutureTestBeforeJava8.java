package CompletableFutureTest;

import java.util.concurrent.*;

public class FutureTestBeforeJava8 {
    public static void main(String[] args) {

        /**
         * 자바 8 이전의 Future 코드
         */
        ExecutorService executor = Executors.newCachedThreadPool();

        Future<Long> future = executor.submit(new Callable<Long>() {

            @Override
            public Long call() throws Exception {

                System.out.println("Callable called.");
                System.out.println("Callable thread: " + Thread.currentThread());

                long sum = 0;

                for (long i = 0; i < 200000000; i += 1) {
                    sum += i;
                }

                System.out.println("Callable thread end: " + Thread.currentThread());

                return sum;
            }
        });

        try {
            System.out.println("Main thread: " + Thread.currentThread());
            Thread.sleep(1000);
            System.out.println("Main thread end: " + Thread.currentThread());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("execute something else");

        try {

            System.out.println("future before : ");
            Long result = future.get(2000, TimeUnit.MILLISECONDS);
            System.out.println("future result : " + result);

        } catch (ExecutionException ee) {
            ee.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (TimeoutException te) {
            te.printStackTrace();
        }

        // https://ocwokocw.tistory.com/61 참고

        /** 출력
         Callable thread: Thread[pool-1-thread-1,5,main]
         Main thread: Thread[main,5,main]
         Callable thread end: Thread[pool-1-thread-1,5,main]
         Main thread end: Thread[main,5,main]
         execute something else
         future before :
         future result : 19999999900000000
         end
         */

        // Main thread를 sleep 할 동안 Callable 코드가 수행되었다.
        // 두 스레드는 다른 스레드임을 보여주었고, get을 호출하겨 결과값을 도출하였다.
        // Main thread를 잠시 멈추어도 Callable thread가 실행됨을 알 수 있다.

        executor.shutdown();

        System.out.println("end");
    }


}
