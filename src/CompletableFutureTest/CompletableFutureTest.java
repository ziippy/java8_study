package CompletableFutureTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CompletableFutureTest {
    public static void main(String[] args) {

        /**
         * 안정적인 비동기 처리를 위해서 CompletableFuture 를 사용하곤 한다.
         */

        // Runnable 은 익명 클래스로 Lambda 로 던질 수 있다.
        // 리턴 타입은 따로 없고, 그냥 Task 를 받아서 소비하는 역할.
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("111 : " + Thread.currentThread().getName() + ": hi");
            // 출력 // 111 : ForkJoinPool.commonPool-worker-1: hi
        });


        // 직접 쓰레드를 컨트롤 하고 싶으면?
        // Executor 를 파라미터로 보내면 된다. 그러면 내가 정의한 쓰레드 풀 내에서 사용된다.
        Executor executor = Executors.newFixedThreadPool(30);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("222 : " + Thread.currentThread().getName() + ": hi");
            // 출력 // 222 : pool-1-thread-1: hi
        }, executor);


        // 리턴값이 필요하면?
        // supplyAsync 를 쓰면 된다.
        System.out.println("333 : " + CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return Thread.currentThread().getName() + ": hi";
            })
        );
        // 그러나, 이렇게 바로 쓰면 다음과 같이 Not Completed 가 리턴된다.
        // 333 : java.util.concurrent.CompletableFuture@27bc2616[Not completed]



        // 그러므로 AllOf 나 AnyOf 를 이용해서 CompletableFuture 들을 하나로 묶어준다.
        // 아래 예는 5초 뒤에 반환하는 메서드를 동시에 3개를 호출하고
        // 3개가 완료되었을때 그 결과를 마지막 thenAcceptAsync 에서 모두 모아서 처리를 한다.
        // 선후 관계가 없는 데이터를 동시에 조회 할때, 적절히 사용할 수 있다.
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "444 : " + Thread.currentThread().getName() + ": hi";
        });
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "555 : " + Thread.currentThread().getName() + ": hi";
        });
        CompletableFuture<String> cf3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "666 : " + Thread.currentThread().getName() + ": hi";
        });

        List<CompletableFuture<String>> completableFutures = Arrays.asList(cf1, cf2, cf3);

        CompletableFuture
                .allOf(completableFutures.toArray(new CompletableFuture[3]))
                .thenApplyAsync(result -> completableFutures.stream().map(future -> future.join()).collect(Collectors.toList()))
                .thenAcceptAsync(messages -> messages.forEach(message -> System.out.println(message)));
        
        // 5초 후 출력 
        // 444 : ForkJoinPool.commonPool-worker-3: hi
        // 555 : ForkJoinPool.commonPool-worker-4: hi
        // 666 : ForkJoinPool.commonPool-worker-5: hi

        try {
            Thread.sleep(11 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("end");


        //////////////////////////////////////////////////////////////
        // 위 코드들이 실행되도록 그냥 대기 하는 함수. e 가 입력되면 종료한다.
//        Scanner sc = new Scanner(System.in);
//        while (true) {
//            char c = sc.nextLine().charAt(0);
//            if (c == 'e') {
//                break;
//            }
//        }
    }


}
