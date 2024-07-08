import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.manager.enums.Status;
import task.manager.model.Epic;
import task.manager.model.SubTask;
import task.manager.model.Task;
import task.manager.service.Managers;
import task.manager.service.taskManager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    HttpTaskServer server;
    Gson gson = Managers.getGson();
    TaskManager manager;
    Task task;
    Task taskWithIntersection;
    Task taskWithRightId;
    Task taskWithWrongId;
    Epic epic;
    Epic epicWithRightId;
    Epic epicWithWrongId;
    SubTask subTask;
    SubTask subTaskWithIntersection;
    SubTask subTaskWithRightId;
    SubTask subTaskWithWrongId;


    void createTasks() {
        task = new Task("Тестовая задача",
                Status.NEW,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                Duration.ofMinutes(15));
        taskWithIntersection = new Task(2,
                "Тестовая задача",
                Status.NEW,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(5),
                Duration.ofMinutes(15));
        taskWithRightId = new Task(
                1,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plus(Duration.ofMinutes(30)),
                Duration.ofMinutes(15));
        taskWithWrongId = new Task(
                100,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plus(Duration.ofMinutes(60)),
                Duration.ofMinutes(15));
        manager.create(task);
    }

    void createEpics() {
        epic = new Epic(
                1,
                "Тестовая задача",
                Status.NEW,
                "Описание тестовой задачи");
        epicWithRightId = new Epic(
                1,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи");
        epicWithWrongId = new Epic(
                100,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи");
        manager.create(epic);
    }

    void createSubTasks() {
        createEpics();
        subTask = new SubTask(2,
                "Тестовая задача",
                Status.NEW,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                Duration.ofMinutes(15),
                1);
        subTaskWithIntersection = new SubTask(
                3,
                "Тестовая задача",
                Status.NEW,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(5),
                Duration.ofMinutes(15),
                1
        );
        subTaskWithRightId = new SubTask(
                2,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plus(Duration.ofMinutes(30)),
                Duration.ofMinutes(15),
                1);
        subTaskWithWrongId = new SubTask(
                100,
                "Тестовая задача",
                Status.IN_PROGRESS,
                "Описание тестовой задачи",
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plus(Duration.ofMinutes(60)),
                Duration.ofMinutes(15),
                1
        );
        manager.create(epic.getId(), subTask);
    }

    @BeforeEach
    void startServer() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    void stopServer() throws InterruptedException {
        server.stop(0);
    }

    @Test
    void tasksGet() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        List<Task> actual = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(actual, "Список задач не возвращается");
        assertEquals(1, actual.size(), "Возвращено неверное количество задач");
        assertEquals(task, actual.get(0), "Возвращена неверная задача");
    }

    @Test
    void tasksGetByID() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        Task actual = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertNotNull(actual, "Задача не возвращается");

        assertEquals(task, actual, "Возвращена неверная задача");
    }

    @Test
    void tasksGetByWrongID() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Задачи с ID " + 2 + " не существует", response.body(),
                "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksGetByNotID() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/2d");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), response.body());

        assertEquals("Неверный запрос", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksPostCreate() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Задача успешно добавлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksPostCreateWithIntersection() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskWithIntersection)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(406, response.statusCode(), response.body());

        assertEquals("Обнаружено пересечение времени выполнения задачи. " +
                "Нельзя выполнять несколько задач одновременна. Задача не добавлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksPostUpdate() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskWithRightId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Задача успешно обновлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksPostUpdateByWrongID() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(taskWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Задача с id " + taskWithWrongId.getId() + " не существует, обновление задачи не выполнено",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksPostUpdateWithIntersection() throws IOException, InterruptedException {
        createTasks();
        manager.create(taskWithRightId);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(406, response.statusCode(), response.body());

        assertEquals("Задача с началом в " + task.getStartTime() + " уже существует. " +
                        "Не может быть нескольких задач с одинаковым временем начала.",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksDeleteById() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        assertEquals("Задача с id " + task.getId() + " успешно удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void tasksDeleteByWrongId() throws IOException, InterruptedException {
        createTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/tasks/" + taskWithWrongId.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Задачи с id " + taskWithWrongId.getId() + " не существует. Задача не была удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }


    @Test
    void epicsGet() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        List<Epic> actual = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        assertNotNull(actual, "Список задач не возвращается");
        assertEquals(1, actual.size(), "Возвращено неверное количество задач");
        assertEquals(epic, actual.get(0), "Возвращена неверная задача");
    }

    @Test
    void epicsGetByID() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/1");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        Epic actual = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());

        assertNotNull(actual, "Задача не возвращается");

        assertEquals(epic, actual, "Возвращена неверная задача");
    }

    @Test
    void epicsGetByWrongID() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Эпика с ID " + 2 + " не существует", response.body(),
                "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsGetByNotID() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/2d");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), response.body());

        assertEquals("Неверный запрос", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsPostCreate() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Эпик успешно добавлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsPostUpdate() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicWithRightId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Эпик успешно обновлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsPostUpdateByWrongID() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/" + epicWithWrongId.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Эпика с id " + epicWithWrongId.getId() + " не существует, обновление эпика не выполнено",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsDeleteById() throws IOException, InterruptedException {
        createEpics();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/1");
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        assertEquals("Эпик с id " + epic.getId() + " успешно удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void epicsDeleteByWrongId() throws IOException, InterruptedException {
        createEpics();
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/epics/" + epicWithWrongId.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Эпика с id " + epicWithWrongId.getId() + " не существует. Задача не была удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksGet() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        List<SubTask> actual = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        assertNotNull(actual, "Список задач не возвращается");
        assertEquals(1, actual.size(), "Возвращено неверное количество задач");
        assertEquals(subTask, actual.get(0), "Возвращена неверная задача");
    }

    @Test
    void subtasksGetByID() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        SubTask actual = gson.fromJson(response.body(), new TypeToken<SubTask>() {
        }.getType());

        assertNotNull(actual, "Задача не возвращается");

        assertEquals(subTask, actual, "Возвращена неверная задача");
    }

    @Test
    void subtasksGetByWrongID() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Подзадачи с ID " + 3 + " не существует", response.body(),
                "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksGetByNotID() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/2d");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(400, response.statusCode(), response.body());

        assertEquals("Неверный запрос", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksPostCreate() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Подзадача успешно добавлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksPostCreateWithIntersection() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskWithIntersection)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(406, response.statusCode(), response.body());

        assertEquals("Обнаружено пересечение времени выполнения задачи. " +
                "Нельзя выполнять несколько задач одновременна. Задача не добавлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksPostUpdate() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskWithRightId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, response.statusCode(), response.body());

        assertEquals("Подзадача успешно обновлена", response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksPostUpdateByWrongID() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTaskWithWrongId)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Подзадачи с id " + subTaskWithWrongId.getId() + " не существует, обновление подзадачи не выполнено",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksPostUpdateWithIntersection() throws IOException, InterruptedException {
        createSubTasks();
        manager.create(epic.getId(), subTaskWithRightId);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(406, response.statusCode(), response.body());

        assertEquals("Задача с началом в " + subTask.getStartTime() + " уже существует. " +
                        "Не может быть нескольких задач с одинаковым временем начала.",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksDeleteById() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, response.statusCode(), response.body());

        assertEquals("Подзадача с id " + subTask.getId() + " успешно удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void subtasksDeleteByWrongId() throws IOException, InterruptedException {
        createSubTasks();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/subtasks/" + subTaskWithWrongId.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(404, response.statusCode(), response.body());

        assertEquals("Подзадачи с id " + subTaskWithWrongId.getId() + " не существует. Задача не была удалена",
                response.body(), "Возвращён неверный ответ: " + response.body());
    }

    @Test
    void historyGet() throws IOException, InterruptedException {
        createTasks();
        manager.getTask(1);

        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/history");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode(), response.body());

        List<Task> expected = manager.getManagerHistory();
        List<Task> actual = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(expected, actual, "История возвращена неверно");
    }

    @Test
    void prioritizedGet() throws IOException, InterruptedException {
        createTasks();

        URI uri = URI.create("http://localhost:8080/api/v1/task-manager/prioritized");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode(), response.body());

        List<Task> expected = manager.getPrioritizedTasksAndSubTasks();
        List<Task> actual = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(expected, actual, "Список задач возвращён неверно");
    }

}