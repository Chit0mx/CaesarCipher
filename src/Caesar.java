import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class Caesar extends JFrame implements ActionListener {
    public static JTextField txtOffset;
    public static JTextArea txtInput;
    public static JTextArea txtOutput;
    private static JScrollPane scrollOutput;

    public static JLabel txtCant;
    public static JLabel txtSO;
    public static JLabel txtOr;
    public static JLabel secTime;
    public static JLabel fyTime;
    public static JLabel esTime;
    public static JButton btnCifrar;
    public static JButton btnForkJoin;
    public static JButton btnExecutorService;
    public Caesar() {
        setTitle("Merge Sort 19310170");
        setSize(1280, 720);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtCant = new JLabel("Desplazamiento");
        txtCant.setBounds(50, 0, 350, 50);

        txtOffset = new JTextField();
        txtOffset.setBounds(50, 50, 150, 50);

        btnCifrar = new JButton("Secuencial");
        btnCifrar.setBounds(210, 50, 100, 50);
        btnCifrar.setActionCommand("cifrar");
        btnCifrar.addActionListener(this);

        btnForkJoin = new JButton("ForkJoin");
        btnForkJoin.setBounds(330, 50, 100, 50);
        btnForkJoin.setActionCommand("forkjoin");
        btnForkJoin.addActionListener(this);

        btnExecutorService = new JButton("Executor");
        btnExecutorService.setBounds(450, 50, 100, 50);
        btnExecutorService.setActionCommand("executor");
        btnExecutorService.addActionListener(this);

        txtSO = new JLabel("Texto original:");
        txtSO.setBounds(50, 100, 150, 50);

        txtInput = new JTextArea(20, 100);
        txtInput.setLineWrap(true);
        txtInput.setEditable(true);

        JScrollPane scrollInput = new JScrollPane(txtInput);
        scrollInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollInput.setBounds(50, 150, 1180, 225);

        txtOr = new JLabel("Texto cifrado:");
        txtOr.setBounds(50, 370, 150, 50);

        txtOutput = new JTextArea(20, 100);
        txtOutput.setLineWrap(true);
        txtOutput.setEditable(false);

        JScrollPane scrollOutput = new JScrollPane(txtOutput);
        scrollOutput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollOutput.setBounds(50, 420, 1180, 225);

        secTime = new JLabel("Secuencial");
        secTime.setBounds(50, 640, 250, 50);

        fyTime = new JLabel("Fork Join");
        fyTime.setBounds(200, 640, 250, 50);

        esTime = new JLabel("Executor Services");
        esTime.setBounds(350, 640, 250, 50);

        this.add(txtOffset);
        this.add(txtCant);
        this.add(secTime);
        this.add(fyTime);
        this.add(esTime);
        this.add(btnCifrar);
        this.add(btnForkJoin);
        this.add(btnExecutorService);
        this.add(scrollInput);
        this.add(scrollOutput);
        this.add(txtSO);
        this.add(txtOr);

        setVisible(true);
    }

    public static void main(String args[]) {

        new Caesar();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int offset = Integer.parseInt(txtOffset.getText());
        String inputString = txtInput.getText();
        String outputString = "";
        CaesarCipher caesarCipher = new CaesarCipher(inputString, offset);

        switch (e.getActionCommand()) {
            case "cifrar" -> {
                long secIni = System.currentTimeMillis();

                outputString = CaesarCipher.cipher(inputString, offset);

                long secFin = System.currentTimeMillis();

                long sTime = (secFin - secIni);
                System.out.println("Sec: "+sTime);
                secTime.setText(sTime+"ms");
            }

            case "forkjoin" -> {
                long fyIni = System.currentTimeMillis();

                ForkJoinPool pool = new ForkJoinPool();

                outputString = pool.invoke(caesarCipher);

                long fyFin = System.currentTimeMillis();
                long fyTm = (fyFin - fyIni);
                System.out.println("FY: "+fyTm);
                fyTime.setText(fyTm+"ms");
            }

            case "executor" -> {
                long esIni = System.currentTimeMillis();

                StringBuilder stringBuilder = new StringBuilder();
                int fourth = inputString.length() / 4;
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                Collection<Callable<String>> callables = new ArrayList<>();

                callables.add(() -> CaesarCipher.cipher(inputString.substring(0, fourth), offset));
                callables.add(() -> CaesarCipher.cipher(inputString.substring(fourth, fourth * 2), offset));
                callables.add(() -> CaesarCipher.cipher(inputString.substring(fourth * 2, fourth * 3), offset));
                callables.add(() -> CaesarCipher.cipher(inputString.substring(fourth * 3), offset));

                try {
                    List<Future<String>> futures =executorService.invokeAll(callables);

                    for (Future<String> future : futures) {
                        stringBuilder.append(future.get());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    throw new RuntimeException(ex);
                }

                outputString = stringBuilder.toString();

                long esFin = System.currentTimeMillis();
                long esTm = (esFin - esIni);
                System.out.println("ES: "+esTm);
                esTime.setText(esTm+"ms");
            }
        }

        txtOutput.setText(outputString);
    }
}