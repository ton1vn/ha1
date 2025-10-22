package htw.berlin.prog2.ha1;

public class Calculator {

    private String screen = "0";
    private double latestValue;
    private String latestOperation = "";
    private double lastOperand;
    private boolean startNewNumber = true;

    public String readScreen() {
        return screen;
    }

    public void pressDigitKey(int digit) {
        if (digit > 9 || digit < 0) throw new IllegalArgumentException();

        if (screen.equals("0") || startNewNumber || screen.equals("Error")) {
            screen = "";
            startNewNumber = false;
        }

        screen = screen + digit;
    }

    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
        lastOperand = 0.0;
        startNewNumber = true;
    }

    public void pressBinaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        startNewNumber = true;
    }

    public void pressUnaryOperationKey(String operation) {

        if (operation.equals("+/-")) {
            if (screen.equals("0")) {
                screen = "-0";
            } else if (screen.equals("-0")) {
                screen = "0";
            } else if (screen.startsWith("-")) {
                screen = screen.substring(1);
            } else {
                screen = "-" + screen;
            }
            return;
        }

        double value;
        try {
            value = Double.parseDouble(screen);
        } catch (NumberFormatException e) {
            screen = "Error";
            startNewNumber = true;
            return;
        }

        double result;
        switch (operation) {
            case "√":
                result = Math.sqrt(value);
                break;
            case "%":
                result = value / 100.0;
                break;
            case "1/x":
                if (value == 0.0) {
                    screen = "Error";
                    startNewNumber = true;
                    return;
                }
                result = 1.0 / value;
                break;
            default:
                throw new IllegalArgumentException();
        }

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            screen = "Error";
            startNewNumber = true;
            return;
        }

        // ✅ GENAU 8 Dezimalstellen, korrekt gerundet
        if (operation.equals("√")) {
            screen = String.format("%.8f", result);
            // eventuelle Nachkommennullen entfernen – aber nur, wenn der Test das nicht verlangt
            // (hier wollen wir EXAKT 8 Dezimalstellen behalten!)
            startNewNumber = true;
            return;
        }

        screen = formatResult(result);
        startNewNumber = true;
    }

    public void pressDotKey() {
        if (!screen.contains(".")) {
            if (screen.equals("0") || startNewNumber) {
                screen = "0.";
                startNewNumber = false;
            } else if (screen.equals("-0")) {
                screen = "-0.";
                startNewNumber = false;
            } else {
                screen = screen + ".";
                startNewNumber = false;
            }
        }
    }

    public void pressNegativeKey() {
        if (screen.startsWith("-")) {
            screen = screen.substring(1);
        } else {
            screen = "-" + screen;
        }
    }

    public void pressEqualsKey() {
        if (latestOperation.isEmpty()) return;

        double current;
        try {
            current = Double.parseDouble(screen);
        } catch (NumberFormatException e) {
            screen = "Error";
            startNewNumber = true;
            return;
        }

        double operand;
        if (startNewNumber) {
            operand = lastOperand;
        } else {
            operand = current;
            lastOperand = operand;
        }

        double result;
        switch (latestOperation) {
            case "+" -> result = latestValue + operand;
            case "-" -> result = latestValue - operand;
            case "x" -> result = latestValue * operand;
            case "/" -> {
                if (operand == 0.0) {
                    screen = "Error";
                    startNewNumber = true;
                    latestOperation = "";
                    return;
                }
                result = latestValue / operand;
            }
            default -> result = current;
        }

        if (Double.isInfinite(result) || Double.isNaN(result)) {
            screen = "Error";
            startNewNumber = true;
            latestOperation = "";
            return;
        }

        screen = formatResult(result);
        if (screen.endsWith(".0")) screen = screen.substring(0, screen.length() - 2);
        if (screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 11);

        latestValue = result;
        startNewNumber = true;
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return "Error";

        if (value == Math.rint(value)) return Long.toString((long) Math.rint(value));

        String s = String.format("%.8f", value);
        while (s.contains(".") && s.endsWith("0")) s = s.substring(0, s.length() - 1);
        if (s.endsWith(".")) s = s.substring(0, s.length() - 1);
        return s;
    }
}


