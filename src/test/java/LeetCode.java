public class LeetCode {
    public int findWinningPlayer(int[] skills, int k) {
        int[] users = new int[skills.length];
        for (int i = 0; i < skills.length; i++){
            users[i] = i;
        }
        int cnt = 0;
        while (true){
            if (skills[0]>skills[1]){
                shiftArrayToEnd(skills,skills[1]);
                shiftArrayToEnd(users,users[1]);
                cnt++;
                if (cnt == k){
                    return users[0];
                }
            }else {
                shiftArrayToEnd(skills,skills[0]);
                shiftArrayToEnd(users,users[0]);
                cnt=0;
            }
        }

    }

    public static void shiftArrayToEnd(int[] array, int shiftBy) {
        if (array == null || shiftBy < 0 || shiftBy >= array.length) {
            throw new IllegalArgumentException("Invalid shiftBy value");
        }
        int length = array.length;
        int[] temp = new int[shiftBy];

        // 将要移动的部分复制到临时数组
        System.arraycopy(array, 0, temp, 0, shiftBy);

        // 将剩余部分向前移动
        System.arraycopy(array, shiftBy, array, 0, length - shiftBy);

        // 将临时数组的元素复制到数组末尾
        System.arraycopy(temp, 0, array, length - shiftBy, shiftBy);
    }

    public boolean checkRecord(String s) {
        int absent = 0;
        int late = 0;
        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) == 'A'){
                absent++;
                if (absent==2){
                    return false;
                }
            }
            if (s.charAt(i) == 'L'){
                late++;
                if (late==3){
                    return false;
                }
            }else late=0;
        }
        return true;
    }
}
