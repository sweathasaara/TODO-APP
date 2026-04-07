import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class Todo {
    int id;
    String task;
    String priority;
    boolean isCompleted;
    Date startDate;
    Date dueDate;

    public Todo(int id, String task, String priority, Date startDate, Date dueDate) {
        this.id = id;
        this.task = task;
        this.priority = priority;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.isCompleted = false;
    }
}

class User {
    String username;
    String password;
    List<Todo> todos;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.todos = new ArrayList<>();
    }
}

public class TodoApp {

    static Map<String, User> users = new HashMap<>();
    static User currentUser = null;
    static int idCounter = 1;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            if (currentUser == null) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice = getSafeInt();

                switch (choice) {
                    case 1: register(); break;
                    case 2: login(); break;
                    case 3: System.exit(0);
                    default: System.out.println("Invalid choice");
                }
            } else {
                userMenu();
            }
        }
    }

    // SAFE INPUT FIX
    static int getSafeInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
    }

    static void register() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        if (users.containsKey(username)) {
            System.out.println("User already exists");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        users.put(username, new User(username, password));
        System.out.println("Registration successful");
    }

    static void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = users.get(username);

        if (user != null && user.password.equals(password)) {
            currentUser = user;
            System.out.println("Login successful");
        } else {
            System.out.println("Invalid credentials");
        }
    }

    static void userMenu() {
        System.out.println("\n1. Add Todo");
        System.out.println("2. View Todos");
        System.out.println("3. Mark Complete");
        System.out.println("4. Delete Todo");
        System.out.println("5. View Overdue Todos");
        System.out.println("6. Export CSV");
        System.out.println("7. Logout");
        System.out.print("Enter choice: ");

        int choice = getSafeInt();

        switch (choice) {
            case 1: addTodo(); break;
            case 2: viewTodos(); break;
            case 3: markComplete(); break;
            case 4: deleteTodo(); break;
            case 5: viewOverdue(); break;
            case 6: exportToCSV(); break;
            case 7: currentUser = null; System.out.println("Logout successful"); break;
            default: System.out.println("Invalid choice");
        }
    }

    static void addTodo() {
        try {
            System.out.print("Enter task: ");
            String task = sc.nextLine();

            if (task.trim().isEmpty()) {
                throw new Exception("Task cannot be empty");
            }

            System.out.print("Enter priority (HIGH/MEDIUM/LOW): ");
            String priority = sc.nextLine().toUpperCase();

            if (!priority.equals("HIGH") && !priority.equals("MEDIUM") && !priority.equals("LOW")) {
                throw new Exception("Invalid priority");
            }

            System.out.print("Enter due date (dd-MM-yyyy): ");
            String inputDate = sc.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dueDate = sdf.parse(inputDate);

            Date startDate = new Date();

            Todo t = new Todo(idCounter++, task, priority, startDate, dueDate);
            currentUser.todos.add(t);

            System.out.println("Todo added successfully");

        } catch (ParseException e) {
            System.out.println("Invalid date format. Use dd-MM-yyyy");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // TABLE FORMAT OUTPUT
    static void viewTodos() {
        if (currentUser.todos.isEmpty()) {
            System.out.println("No todos available");
            return;
        }

        PriorityQueue<Todo> pq = new PriorityQueue<>(
            (a, b) -> getPriorityValue(b.priority) - getPriorityValue(a.priority)
        );

        pq.addAll(currentUser.todos);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("%-5s %-25s %-10s %-12s %-12s %-12s\n",
                "ID", "TASK", "PRIORITY", "STATUS", "START", "DUE");
        System.out.println("--------------------------------------------------------------------------------");

        while (!pq.isEmpty()) {
            Todo t = pq.poll();

            System.out.printf("%-5d %-25s %-10s %-12s %-12s %-12s\n",
                    t.id,
                    t.task,
                    t.priority,
                    (t.isCompleted ? "Completed" : "Pending"),
                    sdf.format(t.startDate),
                    sdf.format(t.dueDate));
        }

        System.out.println("--------------------------------------------------------------------------------");
    }

    static void markComplete() {
        System.out.print("Enter ID: ");
        int id = getSafeInt();

        for (Todo t : currentUser.todos) {
            if (t.id == id) {
                t.isCompleted = true;
                System.out.println("Marked as completed");
                return;
            }
        }

        System.out.println("Todo not found");
    }

    static void deleteTodo() {
        System.out.print("Enter ID: ");
        int id = getSafeInt();

        Iterator<Todo> it = currentUser.todos.iterator();

        while (it.hasNext()) {
            Todo t = it.next();
            if (t.id == id) {
                it.remove();
                System.out.println("Deleted successfully");
                return;
            }
        }

        System.out.println("Todo not found");
    }

    static void viewOverdue() {
        Date today = new Date();
        boolean found = false;

        System.out.println("\nOverdue Tasks:");

        for (Todo t : currentUser.todos) {
            if (!t.isCompleted && t.dueDate.before(today)) {
                found = true;
                System.out.println(t.id + ". " + t.task + " (OVERDUE)");
            }
        }

        if (!found) {
            System.out.println("No overdue tasks");
        }
    }

    static void exportToCSV() {
        try {
            FileWriter writer = new FileWriter("todos.csv");

            writer.append("ID,Task,Priority,Status\n");

            for (Todo t : currentUser.todos) {
                writer.append(t.id + "," + t.task + "," + t.priority + "," +
                        (t.isCompleted ? "Completed" : "Pending") + "\n");
            }

            writer.close();
            System.out.println("Exported to todos.csv");

        } catch (IOException e) {
            System.out.println("File error occurred");
        }
    }

    static int getPriorityValue(String p) {
        switch (p) {
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }
}