package models;

import java.util.*;
import play.data.validation.Constraints.*;

public class Task {
	public Long id;

	@Required
	public String label;

	public static long lastId = 0;
	public static Map<Long,Task> lookup = new HashMap<Long,Task>();

	public static List<Task> all() {
		return new ArrayList<Task>(lookup.values());
	}

	public static void create(Task task) {
		task.id = new Long(lastId++);
		lookup.put(task.id, task);
	}

	public static void delete(Long id) {
		lookup.remove(id);
	}
}
