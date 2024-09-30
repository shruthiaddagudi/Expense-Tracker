import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalExpenseTrackerGUI extends JFrame implements ActionListener {
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JTextArea expensesTextArea;
    private Map<String, List<Double>> expenses;

    public PersonalExpenseTrackerGUI() {
        setTitle("Personal Expense Tracker");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize the expense categories and data structure
        String[] categories = {"Food", "Transport", "Entertainment", "Others"};
        expenses = new HashMap<>();

        for (String category : categories) {
            expenses.put(category, new ArrayList<>());
        }

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>(categories);

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(this);

        inputPanel.add(amountLabel);
        inputPanel.add(amountField);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        // Expenses Display Area
        expensesTextArea = new JTextArea();
        expensesTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(expensesTextArea);

        add(scrollPane, BorderLayout.CENTER);

        // Control Buttons
        JPanel controlPanel = new JPanel();

        JButton viewButton = new JButton("View Expenses");
        viewButton.addActionListener(this);

        JButton deleteButton = new JButton("Delete Last Expense");
        deleteButton.addActionListener(this);

        JButton summaryButton = new JButton("Expense Summary");
        summaryButton.addActionListener(this);

        controlPanel.add(viewButton);
        controlPanel.add(deleteButton);
        controlPanel.add(summaryButton);

        add(controlPanel, BorderLayout.SOUTH);

        loadExpenses(); // Load expenses from file on startup
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Add Expense":
                addExpense();
                break;
            case "View Expenses":
                viewExpenses();
                break;
            case "Delete Last Expense":
                deleteLastExpense();
                break;
            case "Expense Summary":
                showSummary();
                break;
        }
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = (String) categoryComboBox.getSelectedItem();

            expenses.get(category).add(amount);
            saveExpenses(); // Save expenses to file after adding
            amountField.setText("");
            JOptionPane.showMessageDialog(this, "Expense added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
        }
    }

    private void viewExpenses() {
        expensesTextArea.setText("");
        for (Map.Entry<String, List<Double>> entry : expenses.entrySet()) {
            String category = entry.getKey();
            List<Double> amounts = entry.getValue();

            expensesTextArea.append(category + ":\n");
            for (Double amount : amounts) {
                expensesTextArea.append(" - $" + amount + "\n");
            }
            expensesTextArea.append("\n");
        }
    }

    private void deleteLastExpense() {
        String category = (String) categoryComboBox.getSelectedItem();
        List<Double> categoryExpenses = expenses.get(category);

        if (!categoryExpenses.isEmpty()) {
            categoryExpenses.remove(categoryExpenses.size() - 1);
            saveExpenses(); // Save expenses to file after deletion
            JOptionPane.showMessageDialog(this, "Last expense in " + category + " deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "No expenses to delete in " + category + ".");
        }
    }

    private void showSummary() {
        expensesTextArea.setText("");
        double totalExpenses = 0;

        for (Map.Entry<String, List<Double>> entry : expenses.entrySet()) {
            String category = entry.getKey();
            List<Double> amounts = entry.getValue();

            double categoryTotal = amounts.stream().mapToDouble(Double::doubleValue).sum();
            totalExpenses += categoryTotal;

            expensesTextArea.append(category + " Total: $" + categoryTotal + "\n");
        }

        expensesTextArea.append("\nTotal Expenses: $" + totalExpenses);
    }

    private void saveExpenses() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("expenses.txt"))) {
            for (Map.Entry<String, List<Double>> entry : expenses.entrySet()) {
                String category = entry.getKey();
                List<Double> amounts = entry.getValue();

                for (Double amount : amounts) {
                    writer.println(category + ":" + amount);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving expenses: " + e.getMessage());
        }
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader("expenses.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String category = parts[0];
                double amount = Double.parseDouble(parts[1]);

                expenses.get(category).add(amount);
            }
        } catch (IOException e) {
            // Ignore if file does not exist, start fresh
        }
    }

    public static void main(String[] args) {
        new PersonalExpenseTrackerGUI();
    }
}
