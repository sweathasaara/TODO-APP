import java.util.*;

class Todo {
    int id;
    String task;
    String priority;
    boolean isCompleted;
    Date dueDate;

    public Todo(int id, String task, String priority, Date dueDate) {
        this.id = id;
        this.task = task;
        this.priority = priority;
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

    public static void main(String[] args) throws Exception {

        while (true) {

            if (currentUser == null) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

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

    static void userMenu() throws Exception {
        System.out.println("\n1. Add Todo");
        System.out.println("2. View Todos");
        System.out.println("3. Mark Complete");
        System.out.println("4. Delete Todo");
        System.out.println("5. View Overdue Todos");
        System.out.println("6. Logout");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1: addTodo(); break;
            case 2: viewTodos(); break;
            case 3: markComplete(); break;
            case 4: deleteTodo(); break;
            case 5: viewOverdue(); break;
            case 6: currentUser = null;  System.out.println("Logout successful"); break;
            default: System.out.println("Invalid choice");
        }
    }

    static void addTodo() throws Exception {

        System.out.print("Enter task: ");
        String task = sc.nextLine();

        System.out.print("Enter priority (HIGH/MEDIUM/LOW): ");
        String priority = sc.nextLine().toUpperCase();

        System.out.print("Enter due date (yyyy-mm-dd): ");
        String dateInput = sc.nextLine();

        Date dueDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateInput);

        Todo todo = new Todo(idCounter++, task, priority, dueDate);
        currentUser.todos.add(todo);

        System.out.println("Todo added");
    }

    static void viewTodos() {

        if (currentUser.todos.isEmpty()) {
            System.out.println("No todos available");
            return;
        }

        PriorityQueue<Todo> pq = new PriorityQueue<>(
            (a, b) -> getPriorityValue(b.priority) - getPriorityValue(a.priority)
        );

        pq.addAll(currentUser.todos);

        System.out.println("\nYour Todos:");

        while (!pq.isEmpty()) {
            Todo t = pq.poll();
            System.out.println(
                t.id + ". " + t.task + " [" + t.priority + "] - " +
                (t.isCompleted ? "Completed" : "Pending")
            );
        }
    }

    static void markComplete() {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();

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
        int id = sc.nextInt();

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

        for (Todo t : currentUser.todos) {
            if (!t.isCompleted && t.dueDate.before(today)) {
                found = true;
                System.out.println(
                    t.id + ". " + t.task + " [" + t.priority + "] - OVERDUE"
                );
            }
        }

        if (!found) {
            System.out.println("No overdue tasks");
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