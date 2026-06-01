import org.springframework.util.AntPathMatcher;
public class TestAntPathMatcher {
    public static void main(String[] args) {
        AntPathMatcher matcher = new AntPathMatcher();
        System.out.println("Match 1: " + matcher.match("/api/health-packages/**", "/api/health-packages"));
        System.out.println("Match 2: " + matcher.match("/api/health-packages/**", "/api/health-packages/"));
        System.out.println("Match 3: " + matcher.match("/api/health-packages", "/api/health-packages"));
    }
}
