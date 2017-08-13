package ru.ifmo.ctddev.isaev.studentsdb.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.DescriptionGenerator;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ifmo.ctddev.isaev.studentsdb.ComboBoxAggregator;
import ru.ifmo.ctddev.isaev.studentsdb.controller.DemoDbPopulator;
import ru.ifmo.ctddev.isaev.studentsdb.dao.StudentDao;
import ru.ifmo.ctddev.isaev.studentsdb.entity.Student;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vaadin.shared.data.sort.SortDirection.ASCENDING;
import static java.lang.String.format;


@SpringUI
@Title("Военно-морской институт")
@Theme("valo")
public class MainUI extends UI {

    private final StudentDao studentDao;

    private final StudentEditor editor;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TextField lastNameFilter = new TextField("ФИО");

    private final ComboBox<String> militaryRankFilter = new ComboBox<>("Воинское звание");

    private final TextField militaryUnitFilter = new TextField("Воинская часть");

    private final TextField graduationYearFilter = new TextField("Выпуск");

    private final Image logo1 = new Image("", new FileResource(new File(
            "src/main/resources/icons/logo1.png"
    )));

    private final Image logo2 = new Image("", new FileResource(new File(
            "src/main/resources/icons/logo2.png"
    )));

    private final ComboBox<String> fleetFilter = new ComboBox<>("Флот");

    private final Grid<Student> grid;

    private String placeHolderImagebase64;

    private volatile List<Student> allStudents;

    private final Button addNewButton = new Button("Добавить в/сл");

    private VerticalLayout mainUILayout;

    private VerticalLayout mainLayout;

    private final ComboBoxAggregator comboBoxAggregator = new ComboBoxAggregator();

    @Autowired
    public MainUI(DemoDbPopulator populator, StudentDao studentDao) throws IOException {
        this.studentDao = studentDao;
        this.editor = new StudentEditor(studentDao, this);
        this.grid = new Grid<>();
        this.placeHolderImagebase64 = Base64.getEncoder().encodeToString(
                Files.readAllBytes(Paths.get("src/main/resources/icons/photo_placeholder.jpg"))
        );
        //populator.populate(500);
        logo1.setHeight("77px");
        logo2.setHeight("77px");
    }

    public ComboBoxAggregator getComboBoxAggregator() {
        return comboBoxAggregator;
    }

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        grid.setWidth("100%");
        grid.setHeight("500px");
        grid.setRowHeight(75.0);
        GridLayout logos = new GridLayout(3, 1);
        logos.setWidth("100%");
        Label headerLabel = new Label(
                "<h3><center>Военно-морской институт<br>" +
                        "«Военный учебно-научный центр Военно-Морского Флота»<br>" +
                        "«Военно-морская академия имени Адмирала Флота Советского Союза Н.Г.Кузнецова»</center></h3>",
                ContentMode.HTML);
        logos.addComponent(logo2, 0, 0);
        logos.setComponentAlignment(logo2, Alignment.TOP_LEFT);
        logos.addComponent(headerLabel, 1, 0);
        logos.setComponentAlignment(headerLabel, Alignment.MIDDLE_CENTER);
        logos.addComponent(logo1, 2, 0);
        logos.setComponentAlignment(logo1, Alignment.TOP_RIGHT);

        GridLayout header = new GridLayout(6, 1);
        header.setWidth("100%");
        header.addComponent(lastNameFilter, 0, 0);
        header.setComponentAlignment(lastNameFilter, Alignment.TOP_LEFT);
        header.addComponent(graduationYearFilter, 1, 0);
        header.setComponentAlignment(graduationYearFilter, Alignment.TOP_LEFT);
        header.addComponent(militaryUnitFilter, 2, 0);
        header.setComponentAlignment(militaryUnitFilter, Alignment.TOP_LEFT);
        header.addComponent(militaryRankFilter, 3, 0);
        header.setComponentAlignment(militaryRankFilter, Alignment.TOP_LEFT);
        header.addComponent(fleetFilter, 4, 0);
        header.setComponentAlignment(fleetFilter, Alignment.TOP_LEFT);
        header.addComponent(addNewButton, 5, 0);
        header.setComponentAlignment(addNewButton, Alignment.BOTTOM_RIGHT);


        this.mainUILayout = new VerticalLayout(logos, header, grid);
        this.mainLayout = new VerticalLayout(editor.getMainLayout(), mainUILayout);
        mainLayout.setMargin(false);
        mainUILayout.setMargin(false);
        mainUILayout.setExpandRatio(grid, 1.0f);
        mainUILayout.setWidth("100%");
        setContent(mainLayout);

        //Grid.Column idColumn = grid.addColumn(Student::getId).setCaption("ID");
        Column<Student, String> photoColumn = grid.addColumn(
                this::getGridPicture,
                new HtmlRenderer())
                .setCaption("Фото")
                .setWidth(110.0)
                .setSortable(false)
                .setResizable(false);

        Column<Student, String> fioColumn = grid.addColumn(this::formatFIO)
                .setCaption("ФИО")
                .setWidth(300.0)
                .setResizable(false);
        grid.addColumn(student -> dateFormat.format(student.getDateOfBirth()))
                .setCaption("Дата рождения")
                .setWidth(140.0)
                .setResizable(false);
        grid.addColumn(this::formatMilitaryRank)
                .setCaption("В/зв")
                .setWidth(180.0)
                .setResizable(false);
        grid.addColumn(Student::getMilitaryUnit)
                .setCaption("Воинская часть")
                .setWidth(300.0)
                .setDescriptionGenerator((DescriptionGenerator<Student>) Student::getMilitaryUnit)
                .setResizable(false);
        grid.addColumn(Student::getGraduationYear)
                .setCaption("Выпуск")
                .setWidth(85.0)
                .setResizable(false);
        grid.addColumn(this::formatMilitaryRankAssignment)
                .setCaption("Присв. зв")
                .setWidth(136.0)
                .setResizable(false)
                .setSortable(false);
        grid.addColumn(Student::getNationality)
                .setCaption("Нац.")
                .setWidth(100.0)
                .setResizable(false);
        grid.addColumn(Student::getFleet)
                .setCaption("Флот")
                .setResizable(false)
                .setWidth(75.0);
        grid.addColumn(Student::getAchievementList)
                .setCaption("Послужной список")
                .setWidth(300.0)
                .setResizable(false)
                .setSortable(false);
        grid.addColumn(Student::getPosition)
                .setCaption("Должность")
                .setResizable(false)
                .setWidth(300.0)
                .setSortable(false);
        grid.addColumn(this::formatUniversity)
                .setResizable(false)
                .setWidth(300.0)
                .setCaption("Окончил ВУЗ")
                .setDescriptionGenerator((DescriptionGenerator<Student>) this::formatUniversity);
        grid.addColumn(Student::getAveragePoints)
                .setResizable(false)
                .setWidth(100.0)
                .setCaption("Ср. балл атт.");
        grid.addColumn(Student::getForeignLanguage)
                .setCaption("Ин. яз.")
                .setResizable(false)
                .setWidth(100.0);
        grid.addColumn(Student::getIdentificationSeriesNumber)
                .setCaption("Серия и номер удост. личн.")
                .setResizable(false)
                .setWidth(200.0)
                .setSortable(false);
        grid.addColumn(Student::getPersonalNumber)
                .setCaption("Личный номер")
                .setResizable(false)
                .setWidth(200.0)
                .setSortable(false);
        grid.addColumn(this::formatAdmission)
                .setResizable(false)
                .setWidth(100.0)
                .setCaption("Форма допуска");
        grid.addColumn(Student::getPassportNumber)
                .setCaption("Паспорт допуска")
                .setResizable(false)
                .setWidth(100.0)
                .setSortable(false);
        grid.addColumn(this::formatPassportIssue)
                .setCaption("Выдан")
                .setWidth(300.0)
                .setResizable(false)
                .setSortable(false);
        grid.addColumn(student -> student.getInternationalPassport() ? "+" : "")
                .setCaption("Загранпаспорт")
                .setResizable(false)
                .setWidth(50.0)
                .setSortable(false);
        grid.addColumn(Student::getFamilyInfo)
                .setCaption("Ф.И.О чл. семьи (№свид. о браке и рожд., кем, когда выд.)")
                .setResizable(false)
                .setWidth(300.0)
                .setSortable(false);
        grid.addColumn(Student::getWifeNationality)
                .setResizable(false)
                .setWidth(100.0)
                .setCaption("Гр. жены");
        grid.addColumn(Student::getAddress)
                .setCaption("Адрес")
                .setWidth(300.0)
                .setResizable(false)
                .setSortable(false);
        grid.addColumn(Student::getStateRewards)
                .setCaption("Гос. нагр.")
                .setResizable(false)
                .setWidth(100.0)
                .setSortable(false);
        grid.addColumn(Student::getDiplomaTopic)
                .setCaption("Направление дипл. работы")
                .setResizable(false)
                .setWidth(100.0)
                .setSortable(false);
        grid.addColumn(Student::getAllocation)
                .setCaption("Распределение")
                .setResizable(false)
                .setWidth(300.0)
                .setSortable(false);
        grid.addColumn(Student::getAdditionalInfo)
                .setResizable(false)
                .setWidth(300.0)
                .setCaption("Примечания");
        grid.setFrozenColumnCount(2);
        grid.setSortOrder(Collections.singletonList(new GridSortOrder<>(fioColumn, ASCENDING)));


        final HasValue.ValueChangeListener updateGrid = valueChangeEvent -> updateGrid();

        // Replace listing with filtered content when user changes lastNameFilter
        lastNameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        lastNameFilter.addValueChangeListener(updateGrid);
        militaryUnitFilter.setValueChangeMode(ValueChangeMode.LAZY);
        militaryUnitFilter.addValueChangeListener(updateGrid);
        militaryRankFilter.addValueChangeListener(updateGrid);
        fleetFilter.addValueChangeListener(updateGrid);
        graduationYearFilter.addValueChangeListener(updateGrid);

        grid.addItemClickListener((ItemClickListener) itemClick -> {
            if (itemClick.getMouseEventDetails().isDoubleClick()) {
                editor.editStudent((Student) itemClick.getItem());
                openEditor();
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewButton.addClickListener(e -> {
            editor.editStudent(new Student());
            openEditor();
        });

        // Initialize listing
        reloadUpdateAndShow();
    }

    public void reloadUpdateAndShow() {
        loadAll();
        updateGrid();
        openGrid();
    }

    public void updateAndShow() {
        updateGrid();
        openGrid();
    }

    public void openGrid() {
        mainUILayout.setVisible(true);
        editor.hide();
    }

    public void openEditor() {
        mainUILayout.setVisible(false);
        editor.makeVisible();
    }

    private void updateGrid() {
        final String lastNameFilterValue = lastNameFilter.getValue() == null ? "" : lastNameFilter.getValue().toLowerCase();
        final String militaryUnitFilterValue = militaryUnitFilter.getValue() == null ? "" : militaryUnitFilter.getValue().toLowerCase();
        final List<Student> filteredItems = allStudents.stream()
                .peek(comboBoxAggregator::refresh)
                .filter(person -> {
                            String onDisplay = String.format("%s %s", person.getLastName(), person.getFirstName());
                            return onDisplay.toLowerCase().contains(lastNameFilterValue);
                        }
                )
                .filter(person -> {
                            final String fieldValue = person.getMilitaryUnit() != null ? person.getMilitaryUnit().toLowerCase() : "";
                            return fieldValue.contains(militaryUnitFilterValue);
                        }
                )
                .filter(person -> militaryRankFilter.getValue() == null || Objects.equals(person.getMilitaryRank(), militaryRankFilter.getValue()))
                .filter(person -> fleetFilter.getValue() == null || Objects.equals(person.getFleet(), fleetFilter.getValue()))
                .filter(person -> graduationYearFilter.getValue() == null ||
                        graduationYearFilter.getValue().isEmpty() ||
                        person.getGraduationYear().startsWith(graduationYearFilter.getValue())
                )
                .collect(Collectors.toList());
        grid.setItems(filteredItems);
        militaryRankFilter.setItems(comboBoxAggregator.getMilitaryRanks());
        fleetFilter.setItems(comboBoxAggregator.getFleets());
    }

    private String formatFIO(Student st) {
        return format("%s\n%s\n%s", st.getLastName(), st.getFirstName(), st.getPatronymic());
    }

    private String formatPassportIssue(Student student) {
        String passportIssuer = student.getPassportIssuer() == null ? "" : student.getPassportIssuer();
        String passportIssueDate = student.getPassportIssueDate() == null ? "" : student.getPassportIssueDate().toString();
        return passportIssuer + "\n" + passportIssueDate;
    }

    private String formatAdmission(Student student) {
        String admissionForm = student.getAdmissionForm() == null ? "" : student.getAdmissionForm();
        String admissionDate = student.getAdmissionDate() == null ? "" : dateFormat.format(student.getAdmissionDate());
        return admissionForm + "\n" + admissionDate;
    }

    private String formatMilitaryRank(Student student) {
        String rankName = student.getMilitaryRank() == null ? "" : student.getMilitaryRank();
        return rankName;
    }

    private String formatMilitaryRankAssignment(Student student) {
        String militaryRankAwardDate = student.getMilitaryRankAwardDate() == null ? "" : dateFormat.format(student.getMilitaryRankAwardDate());
        String militaryRankOrderName = student.getMilitaryRankOrderName() == null ? "" : student.getMilitaryRankOrderName();
        return militaryRankAwardDate + "\n" + militaryRankOrderName;
    }

    private String formatUniversity(Student student) {
        String universityTitle = student.getUniversity() == null ? "" : student.getUniversity();
        String graduationYear = student.getGraduationYear() == null ? "" : String.valueOf(student.getGraduationYear());
        return format("%s в %s г.", universityTitle, graduationYear);
    }


    void loadAll() {
        allStudents = studentDao.findAll();
    }

    private String getGridPicture(Student student) {
        final String imageBase64;
        if (student.getPhotoBase64() != null) {
            imageBase64 = student.getPhotoBase64();
        } else {
            imageBase64 = placeHolderImagebase64;
        }
        return String.format("<img height=\"75px\" width=\"75px\" src=\"data:image/jpeg;base64,%s\" />", imageBase64);
    }

}
