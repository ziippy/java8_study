package count_of_eight;
/**
 * @author ziippy on 2022-03-28.
 * @project java8_study/count_of_8
 */
public class CountOfEight {
    public static void main(String[] args) {
        // NCSoft 면접 보는데 1부터 10000까지 8이 나오는 count 를 구하란다.
        // 손코딩으로..

        // 그냥 막 했을 때
        final int endNumber = 10000;

        int count = 0;
        for (int i = 1; i <= endNumber; i++) {
            String s = String.valueOf(i);

            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '8') {
                    count++;
                }
            }
        }

        System.out.println(count);

        // 뭔가 rule 이 있네.
        // 0~9 까지는 1개
        // 0~99 까지는 20개
        // 0~999 까지는 300개
        // 즉, 최대자리수*자리수 라는 rule 을 알 수 있네

        // 그걸 적용하면
        String s2 = String.valueOf(endNumber - 1);
        int s2Length = s2.length();
        int count2 = (int) Math.pow(10, s2Length - 1) * s2Length;
        System.out.println(count2);
        
        
        // 뭐 손코딩 때는 맨 위 방식으로밖에 못 씀. 그래서 탈락인가.. ㅋ
    }
}
