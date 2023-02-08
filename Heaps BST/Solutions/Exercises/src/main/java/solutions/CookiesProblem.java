package solutions;

import java.util.PriorityQueue;
import java.util.Queue;

public class CookiesProblem {
    public Integer solve(int requiredSweetness, int[] cookies) {
        Queue<Integer> cookiesSweetness = new PriorityQueue<>();

        for (int cookie : cookies) {
            cookiesSweetness.add(cookie);
        }

        int currentMinSweetness = cookiesSweetness.peek();
        int counter = 0;

        while (currentMinSweetness < requiredSweetness) {
            if (cookiesSweetness.size() > 1) {
                int leastSweetCookie = cookiesSweetness.poll();
                int secondLeastSweetCookie = cookiesSweetness.poll();

                int combinedSweetness = leastSweetCookie + (2 * secondLeastSweetCookie);

                cookiesSweetness.add(combinedSweetness);

                currentMinSweetness = cookiesSweetness.peek();

                counter++;
            } else {
                return -1;
            }
        }

        return counter;
    }
}
