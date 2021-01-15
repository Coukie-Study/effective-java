# try-finally보다는 try-with-resources를 사용하라

## try-finally
자바 라이브러리 중에는 close 호출을 통해 직접 닫아줘야하는 자원이 있다.
이러한 자원들을 닫아줄 때, 예전에는 try-finally를 주로 썼다.
```java
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```
### 단점
예외는 try블록과 finally블록에서 모두 발생할 수 있는데, finally에서 발생한 예외가 try 블록의 예외를 덮는 문제가 발생한다.
```java
public class TopLine {
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine(); // 여기 예외가 발생하면
        } finally {
            br.close(); // 여기서 덮어쓴다.
        }
    }

    public static void main(String[] args) throws IOException {
        String path = args[0];
        System.out.println(firstLineOfFile(path));
    }
}
```
## try-with-resources
try-with-resources는 Java 7에서 등장한 개념으로 AutoCloseable을 구현한 자원들을 효율적으로 쓸 수 있도록 하였다.
```java
static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(
            new FileReader(path))) {
        return br.readLine();
    } catch (Exception e) {
        // ...
    }
}
```
위에서 보듯 코드의 가독성이 증가하고, 기존에 close가 예외를 덮어쓰는 문제를 suppressed에 close 예외를 숨겨 기존 예외를 나타낼 수 있도록 했다.
또한 try를 중첩해서 쓰지 않아도 되어 catch 절을 다수의 예외를 효율적으로 처리 가능하다.
