import java.util.*;
class Todo {
    int id;
    String task;
    String priority; 
    boolean isCompleted;
    public Todo(int id, String task, String priority) {
        this.id = id;
        this.task = task;
        this.priority = priority;
        this.isCompleted = false;
    }
}

public class TodoApp {
    static int idCounter = 1;
    static PriorityQueue<Todo> pq = new PriorityQueue<>(
        new Comparator<Todo>() {
            public int compare(Todo a, Todo b) {
                return getPriorityValue(b.priority) - getPriorityValue(a.priority);
            }
        }
    );
    static List<Todo> allTodos = new ArrayList<>();
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Add Todo");
            System.out.println("2. View Todos by Priority");
            System.out.println("3. Mark Todo as Complete");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addTodo(sc);
                    break;

                case 2:
                    viewTodos();
                    break;

                case 3:
                    markComplete(sc);
                    break;

                case 4:
                    System.out.println("Application exited");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    static void addTodo(Scanner sc) {
        System.out.print("Enter task: ");
        String task = sc.nextLine();
        System.out.print("Enter priority (HIGH/MEDIUM/LOW): ");
        String priority = sc.nextLine().toUpperCase();
        if (!priority.equals("HIGH") && !priority.equals("MEDIUM") && !priority.equals("LOW")) {
            System.out.println("Invalid priority. Use HIGH, MEDIUM, or LOW.");
            return;
        }
        Todo todo = new Todo(idCounter++, task, priority);
        pq.add(todo);
        allTodos.add(todo);

        System.out.println("Todo added successfully");
    }
    static void viewTodos() {

        if (pq.isEmpty()) {
            System.out.println("No todos available");
            return;
        }
        PriorityQueue<Todo> temp = new PriorityQueue<>(pq);
        System.out.println("\nTodos (sorted by priority):");
        while (!temp.isEmpty()) {
            Todo t = temp.poll();
            System.out.println(
                t.id + ". " + t.task + " [" + t.priority + "] - " +
                (t.isCompleted ? "Completed" : "Pending")
            );
        }
    }
    static void markComplete(Scanner sc) {

        System.out.print("Enter Todo ID to mark as complete: ");
        int id = sc.nextInt();
        boolean found = false;
        for (Todo t : allTodos) {
            if (t.id == id) {
                t.isCompleted = true;
                found = true;
                System.out.println("Todo marked as completed");
                break;
            }
        }
        if (!found) {
            System.out.println("Todo not found");
        }
    }

    static int getPriorityValue(String priority) {
        switch (priority) {
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }
}