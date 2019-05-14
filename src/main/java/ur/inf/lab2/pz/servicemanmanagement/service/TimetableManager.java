package ur.inf.lab2.pz.servicemanmanagement.service;

import jfxtras.scene.control.agenda.Agenda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ur.inf.lab2.pz.servicemanmanagement.domain.DateRange;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

interface TimetableDatasource {
    Set<Task> getTasksByDateRange(Long leaderId, DateRange peroidOfTime);
}

interface Timetable {

    void loadTasks(Set<Task> tasksFromActualWeek);
    DateRange getDateRange();
    Agenda generate();
}

class ManagerTimetable implements Timetable {
    private DateRange dateRange;
    private Set<Task> rawTasks = new HashSet<>();
    private static final String TASK_STYLE_CLASS_NAME = "task";
    private static final String DESCRIPTION_PATTERN = "{id} \n {tag} \n {description}";
    private static final String[] GLOBAL_STYLE_CLASSES = { "global-font" };
    private Agenda.AppointmentGroup appointmentGroup;

    public ManagerTimetable(DateRange dateRange) {
        this.dateRange = dateRange;
        appointmentGroup = new Agenda.AppointmentGroupImpl().withStyleClass(TASK_STYLE_CLASS_NAME);
    }

    @Override
    public void loadTasks(Set<Task> tasksFromActualWeek) {
        if (tasksFromActualWeek == null)
            throw new NullPointerException();

        rawTasks.addAll(tasksFromActualWeek);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String rawDate = format.format(date);

        return LocalDate.parse(rawDate).atTime(0, 0);
    }

    @Override
    public Agenda generate() {
        Agenda agenda = new Agenda();
        agenda.displayedLocalDateTime().set(toLocalDateTime(dateRange.getFrom()));
        agenda.appointments().addAll(transformTasksToAppointments());

        allowCreatingTasksByDrag(agenda);
        agenda.getStyleClass().addAll(GLOBAL_STYLE_CLASSES);
        return agenda;
    }

    private void allowCreatingTasksByDrag(Agenda agenda) {
        agenda.newAppointmentCallbackProperty().set((dateTimeRange) ->
                new Agenda.AppointmentImplLocal()
                    .withStartLocalDateTime(dateTimeRange.getStartLocalDateTime())
                    .withEndLocalDateTime(dateTimeRange.getEndLocalDateTime())
                    .withAppointmentGroup(appointmentGroup)
        );
    }

    private Collection<? extends Agenda.Appointment> transformTasksToAppointments() {
        return rawTasks.stream()
                .map(rawTask ->
                    new Agenda.AppointmentImplLocal()
                        .withStartLocalDateTime(rawTask.getDateTimeFrom())
                        .withEndLocalDateTime(rawTask.getDateTimeTo())
                        .withDescription(rawTask.getId())
                        .withSummary(prepareTaskDescription(rawTask))
                        .withAppointmentGroup(appointmentGroup))
                .collect(Collectors.toList());
    }

    private String prepareTaskDescription(Task task) {
        return DESCRIPTION_PATTERN.replace("{id}", task.getId())
                .replace("{tag}", task.getTag())
                .replace("{description}", task.getDescription());
    }

    public DateRange getDateRange() {
        return dateRange;
    }

}

interface Task {

    String getId(); // np. M1  (Pierwsza litera taga + numer, następny task z tagiem montaż byłby M2)
    String getTag(); //np. Montaż
    String getDescription();
    LocalDateTime getDateTimeFrom(); //example of construct LocalDate.parse("2019-05-01").atTime(12, 00);
    LocalDateTime getDateTimeTo(); // LocalDate.parse("2019-05-01").atTime(14, 30);

    // przeslon equals i hashcode po id, najlepiej wygeneruj InteliJem
}

@Service
public class TimetableManager {

    @Autowired
    private TimetableDatasource datasource;

    public static DateRange getWeekDateRangeByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Date actualWeekMondaysDate = cal.getTime();
        cal.add(Calendar.DAY_OF_WEEK, 6);

        Date actualWeekSundaysDate = cal.getTime();

        return new DateRange(actualWeekMondaysDate, actualWeekSundaysDate);
    }

    public Agenda load(Long leaderId) {
        Date now = new Date();
        Timetable timetable = new ManagerTimetable(getWeekDateRangeByDate(now));

        Set<Task> tasksFromActualWeek = datasource.getTasksByDateRange(leaderId, timetable.getDateRange());
        timetable.loadTasks(tasksFromActualWeek);
        return timetable.generate();
//
//        Agenda agenda = new Agenda();
//        agenda.displayedLocalDateTime().set(LocalDate.parse("2019-05-01").atTime(2, 10));
//        Agenda.AppointmentGroupImpl taskGroup = new Agenda.AppointmentGroupImpl().withStyleClass("task");
//        Agenda.AppointmentImplLocal task = new Agenda.AppointmentImplLocal()
//                .withStartLocalDateTime(LocalDate.parse("2019-05-01").atTime(4, 00))
//                .withEndLocalDateTime(LocalDate.parse("2019-05-01").atTime(15, 30))
//                .withDescription("It's time")
//                .withSummary("TO JEST ZADANIE KOLEŻKO")
//                .withAppointmentGroup(taskGroup);
//
//        agenda.appointments().addAll(
//                task // you should use a map of AppointmentGroups
//        );
//
//        agenda.setOnDragDropped(val -> {
//            System.out.println(val.getX());
//        });
//
//        agenda.newAppointmentCallbackProperty().set( (localDateTimeRange) -> {
//            return new Agenda.AppointmentImplLocal()
//                    .withStartLocalDateTime(localDateTimeRange.getStartLocalDateTime())
//                    .withEndLocalDateTime(localDateTimeRange.getEndLocalDateTime())
//                    .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("task")); // it is better to have a map of appointment groups to get from
//        });
//
////        agenda.setStyle("-fx-font-size: 40");
//        agenda.getStyleClass().add("global-font");
//        return agenda;
//        viewManager.getStae().getScene().getStylesheets().forEach(stylesheet -> agenda.getStylesheets().add(stylesheet));
    }
}