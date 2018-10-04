package com.company;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.sort;

public class ArrayStaff {
    public static Comparator<Employee> employmentDayComparator = new Comparator<Employee>() {
        @Override
        public int compare(Employee o1, Employee o2) {
            return o1.employmentDay.compareTo(o2.employmentDay);
        }
    };

    public static void main(String[] args) throws IOException, ParseException, JAXBException {

        Path path = Paths.get("c:/temp/employees.xml");
        AllEmployee deserializeXML = new AllEmployee();
        AllEmployee allEmployee = new AllEmployee();


        if(!(Files.exists(path))) {
            System.out.println("Список сотрудников пуст! Введите данные для заполнения списка сотрудников");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String filename = br.readLine();
            FileReader fileReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fileReader);
            ArrayList<String> fromFile = new ArrayList<>();
            ArrayList<Worker> workers = new ArrayList<>();
            ArrayList<Manager> managers = new ArrayList<>();
            ArrayList<Other> others = new ArrayList<>();
            while (reader.ready()) {
                String staff = reader.readLine();
                fromFile.add(staff);
            }

            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            Date date = new Date();

            for (int i = 0; i < fromFile.size() - 5; i++) {
                if (fromFile.get(i).equals("Работник") || fromFile.get(i).equals("\uFEFFРаботник")) {
                    Worker newWorker = new Worker();
                    newWorker.lastName = fromFile.get(i + 1);
                    newWorker.name = fromFile.get(i + 2);
                    newWorker.middleName = fromFile.get(i + 3);
                    date = format.parse(fromFile.get(i + 4));
                    newWorker.birthDay = date;
                    date = format.parse(fromFile.get(i + 5));
                    newWorker.employmentDay = date;
                    newWorker.id = i;
                    workers.add(newWorker);

                }
            }

            for (int i = 0; i < fromFile.size() - 6; i++) {
                if (fromFile.get(i).equals("Другой")) {
                    Other newOther = new Other();
                    newOther.lastName = fromFile.get(i + 1);
                    newOther.name = fromFile.get(i + 2);
                    newOther.middleName = fromFile.get(i + 3);
                    date = format.parse(fromFile.get(i + 4));
                    newOther.birthDay = date;
                    date = format.parse(fromFile.get(i + 5));
                    newOther.employmentDay = date;
                    newOther.discription = fromFile.get(i + 6);
                    others.add(newOther);
                }
            }


            for (int n = 0; n < fromFile.size() - 5; n++) {
                if (fromFile.get(n).equals("Менеджер")) {
                    Manager newManager = new Manager();
                    newManager.lastName = fromFile.get(n + 1);
                    newManager.name = fromFile.get(n + 2);
                    newManager.middleName = fromFile.get(n + 3);
                    date = format.parse(fromFile.get(n + 4));
                    newManager.birthDay = date;
                    date = format.parse(fromFile.get(n + 5));
                    newManager.employmentDay = date;

                    for (int j = 6; j < fromFile.size(); j += 3) {
                        if (n + j > fromFile.size() - 2) {
                            break;
                        }
                        if (!(fromFile.get(n + j).equals("Работник") || fromFile.get(n + j).equals("Менеджер") || fromFile.get(n + j).equals("Другой"))) {
                            for (int k = 0; k < workers.size(); k++) {
                                if (fromFile.get(n + j).equals(workers.get(k).lastName) && fromFile.get(n + j + 1).equals(workers.get(k).name) && fromFile.get(n + j + 2).equals(workers.get(k).middleName))
                                    newManager.subordinate.add(workers.get(k).id);
                            }
                        }
                    }

                    managers.add(newManager);
                }
            }

            allEmployee = new  AllEmployee(workers, others, managers);

            convertToXml(allEmployee);
        }

        else {
            deserializeXML = convertFromXML();
            Scanner s = new Scanner(System.in);

            System.out.println("Введите данные для изменения");
            ArrayList<String> dataUpdate = new ArrayList<>();
            while (s.hasNextLine()){
                String line = s.nextLine();
                if(line.equals("end")){
                    break;
                }
                dataUpdate.add(line);
            }

                if(dataUpdate.get(0).equals("C")){
                    allEmployee = createNewEmployee(deserializeXML);
                    convertToXml(allEmployee);
                }
                else if(dataUpdate.get(0).equals("U")){
                    allEmployee = updateEmployee(dataUpdate, deserializeXML);
                    convertToXml(allEmployee);
                }
                else if(dataUpdate.get(0).equals("D")){
                   allEmployee = deleteEmployee(dataUpdate, deserializeXML);
                   convertToXml(allEmployee);
                }
                else  if(dataUpdate.get(0).equals("SortN")){
                    sortLastName(deserializeXML);
                }

                else if(dataUpdate.get(0).equals("SortD")){
                    sortEmploymentDay(deserializeXML);
                }
                else if(dataUpdate.get(0).equals("BindM")){
                    allEmployee = bindEmployeeToManager(dataUpdate, deserializeXML);
                    convertToXml(allEmployee);
                }


        }

    }


    public static AllEmployee createNewEmployee(AllEmployee deserializeXML) throws IOException, ParseException {
        System.out.println("Введите путь к текстовому файлу: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        FileReader fileReader = new FileReader(line);
        BufferedReader reader = new BufferedReader(fileReader);
        ArrayList<String> fromFile = new ArrayList<>();
        while (reader.ready()) {
            String staff = reader.readLine();
            fromFile.add(staff);
        }
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = new Date();

        for (int i = 0; i < fromFile.size() - 5; i++) {
            if (fromFile.get(i).equals("Работник") || fromFile.get(i).equals("\uFEFFРаботник")) {
                Worker newWorker = new Worker();
                newWorker.lastName = fromFile.get(i + 1);
                newWorker.name = fromFile.get(i + 2);
                newWorker.middleName = fromFile.get(i + 3);
                date = format.parse(fromFile.get(i + 4));
                newWorker.birthDay = date;
                date = format.parse(fromFile.get(i + 5));
                newWorker.employmentDay = date;
                newWorker.id = deserializeXML.wrkr.size()>0 ? deserializeXML.wrkr.get(deserializeXML.wrkr.size()-1).id + 1 : 1;
                deserializeXML.wrkr.add(newWorker);

            }
        }

        for (int i = 0; i < fromFile.size() - 6; i++) {
            if (fromFile.get(i).equals("Другой")) {
                Other newOther = new Other();
                newOther.lastName = fromFile.get(i + 1);
                newOther.name = fromFile.get(i + 2);
                newOther.middleName = fromFile.get(i + 3);
                date = format.parse(fromFile.get(i + 4));
                newOther.birthDay = date;
                date = format.parse(fromFile.get(i + 5));
                newOther.employmentDay = date;
                newOther.discription = fromFile.get(i + 6);
                deserializeXML.othr.add(newOther);
            }
        }


        for (int n = 0; n < fromFile.size() - 5; n++) {
            if (fromFile.get(n).equals("Менеджер")) {
                Manager newManager = new Manager();
                newManager.lastName = fromFile.get(n + 1);
                newManager.name = fromFile.get(n + 2);
                newManager.middleName = fromFile.get(n + 3);
                date = format.parse(fromFile.get(n + 4));
                newManager.birthDay = date;
                date = format.parse(fromFile.get(n + 5));
                newManager.employmentDay = date;

                for (int j = 6; j < fromFile.size(); j += 3) {
                    if (n + j > fromFile.size() - 2) {
                        break;
                    }
                    if (!(fromFile.get(n + j).equals("Работник") || fromFile.get(n + j).equals("Менеджер") || fromFile.get(n + j).equals("Другой"))) {
                        for (int k = 0; k < deserializeXML.wrkr.size(); k++) {
                            if (fromFile.get(n + j).equals(deserializeXML.wrkr.get(k).lastName) && fromFile.get(n + j + 1).equals(deserializeXML.wrkr.get(k).name) && fromFile.get(n + j + 2).equals(deserializeXML.wrkr.get(k).middleName))
                                newManager.subordinate.add(deserializeXML.wrkr.get(k).id);
                        }
                    }
                }

              deserializeXML.mngr.add(newManager);
            }
        }

    return deserializeXML;

    }

    public static AllEmployee deleteEmployee(ArrayList<String> dataUpdate, AllEmployee deserializeXML){
        for (int i = 1; i < dataUpdate.size()-2; i++){
            for (int j = 0; j < deserializeXML.wrkr.size(); j++){
                if(dataUpdate.get(i).equals(deserializeXML.wrkr.get(j).lastName) && dataUpdate.get(i+1).equals(deserializeXML.wrkr.get(j).name) && dataUpdate.get(i+2).equals(deserializeXML.wrkr.get(j).middleName)){
                    deserializeXML.wrkr.remove(j);
                }
            }
            for (int j = 0; j < deserializeXML.mngr.size(); j++){
                if(dataUpdate.get(i).equals(deserializeXML.mngr.get(j).lastName) && dataUpdate.get(i+1).equals(deserializeXML.mngr.get(j).name) && dataUpdate.get(i+2).equals(deserializeXML.mngr.get(j).middleName)){
                    deserializeXML.mngr.remove(j);
                }
            }
            for (int j = 0; j < deserializeXML.othr.size(); j++){
                if(dataUpdate.get(i).equals(deserializeXML.othr.get(j).lastName) && dataUpdate.get(i+1).equals(deserializeXML.othr.get(j).name) && dataUpdate.get(i+2).equals(deserializeXML.othr.get(j).middleName)){
                    deserializeXML.othr.remove(j);
                }
            }
        }

        return deserializeXML;

    }

    public static AllEmployee updateEmployee(ArrayList<String> dataUpdate, AllEmployee deserializeXML){
        Employee employee = new Employee();

            for (int j = 0; j < deserializeXML.wrkr.size(); j++){
                if(dataUpdate.get(1).equals(deserializeXML.wrkr.get(j).lastName) && dataUpdate.get(2).equals(deserializeXML.wrkr.get(j).name) && dataUpdate.get(3).equals(deserializeXML.wrkr.get(j).middleName)){
                    employee.name = deserializeXML.wrkr.get(j).name;
                    employee.lastName = deserializeXML.wrkr.get(j).lastName;
                    employee.middleName = deserializeXML.wrkr.get(j).middleName;
                    employee.birthDay = deserializeXML.wrkr.get(j).birthDay;
                    employee.employmentDay = deserializeXML.wrkr.get(j).employmentDay;
                    deserializeXML.wrkr.remove(j);
                }
            }
            for (int j = 0; j < deserializeXML.mngr.size(); j++){
                if(dataUpdate.get(1).equals(deserializeXML.mngr.get(j).lastName) && dataUpdate.get(2).equals(deserializeXML.mngr.get(j).name) && dataUpdate.get(3).equals(deserializeXML.mngr.get(j).middleName)){
                    employee.name = deserializeXML.mngr.get(j).name;
                    employee.lastName = deserializeXML.mngr.get(j).lastName;
                    employee.middleName = deserializeXML.mngr.get(j).middleName;
                    employee.birthDay = deserializeXML.mngr.get(j).birthDay;
                    employee.employmentDay = deserializeXML.mngr.get(j).employmentDay;
                    deserializeXML.mngr.remove(j);

                }
            }
            for (int j = 0; j < deserializeXML.othr.size(); j++){
                if(dataUpdate.get(1).equals(deserializeXML.othr.get(j).lastName) && dataUpdate.get(2).equals(deserializeXML.othr.get(j).name) && dataUpdate.get(3).equals(deserializeXML.othr.get(j).middleName)){
                    employee.name = deserializeXML.othr.get(j).name;
                    employee.lastName = deserializeXML.othr.get(j).lastName;
                    employee.middleName = deserializeXML.othr.get(j).middleName;
                    employee.birthDay = deserializeXML.othr.get(j).birthDay;
                    employee.employmentDay = deserializeXML.othr.get(j).employmentDay;
                    deserializeXML.othr.remove(j);
                }
            }

            if(dataUpdate.get(dataUpdate.size()-1).equals("Работник")){
                Worker worker = new Worker();
                worker.name = employee.name;
                worker.lastName = employee.lastName;
                worker.middleName = employee.middleName;
                worker.birthDay = employee.birthDay;
                worker.employmentDay = employee.employmentDay;
                worker.id = deserializeXML.wrkr.size()>0 ? deserializeXML.wrkr.get(deserializeXML.wrkr.size()-1).id + 1 : 1;
                deserializeXML.wrkr.add(worker);
            }
        if(dataUpdate.get(dataUpdate.size()-1).equals("Менеджер")){
            Manager manager = new Manager();
            manager.name = employee.name;
            manager.lastName = employee.lastName;
            manager.middleName = employee.middleName;
            manager.birthDay = employee.birthDay;
            manager.employmentDay = employee.employmentDay;
//          чет с подчиненными порешать manager.subordinate
            deserializeXML.mngr.add(manager);
        }

        if(dataUpdate.get(dataUpdate.size()-1).equals("Другой")){
            Other other = new Other();
            other.name = employee.name;
            other.lastName = employee.lastName;
            other.middleName = employee.middleName;
            other.birthDay = employee.birthDay;
            other.employmentDay = employee.employmentDay;
            Scanner s = new Scanner(System.in);
            System.out.println("Введите описание сотрудника: ");
            String line = s.nextLine();
            other.discription = line;
            deserializeXML.othr.add(other);
        }
        return deserializeXML;
    }

    public static AllEmployee bindEmployeeToManager(ArrayList<String> dataUpdate, AllEmployee deserializeXML){
        int id = 0;
        for (int j = 0; j < deserializeXML.wrkr.size(); j++){
            if(dataUpdate.get(1).equals(deserializeXML.wrkr.get(j).lastName) && dataUpdate.get(2).equals(deserializeXML.wrkr.get(j).name) && dataUpdate.get(3).equals(deserializeXML.wrkr.get(j).middleName)){
             id = deserializeXML.wrkr.get(j).id;

            }
        }
        for(int k = 0; k < deserializeXML.mngr.size(); k++){
            for(int y = 0; y < deserializeXML.mngr.get(k).subordinate.size(); y++){
                if(id==deserializeXML.mngr.get(k).subordinate.get(y)){
                    deserializeXML.mngr.get(k).subordinate.remove(y);
                }
            }
        }

        for (int i = 0; i < deserializeXML.mngr.size(); i++){
            if(dataUpdate.get(4).equals(deserializeXML.mngr.get(i).lastName) && dataUpdate.get(5).equals(deserializeXML.mngr.get(i).name) && dataUpdate.get(6).equals(deserializeXML.mngr.get(i).middleName)){
                deserializeXML.mngr.get(i).subordinate.add(id);
            }
        }
        return  deserializeXML;
    }
     //TODO: убрать копипасту
    public static void sortLastName(AllEmployee allEmployee) {
        ArrayList<Employee> emp = new ArrayList<>();
        for (int i = 0; i < allEmployee.wrkr.size(); i++) {
            emp.add(allEmployee.wrkr.get(i));
        }
        for (int i = 0; i < allEmployee.mngr.size(); i++) {
            emp.add(allEmployee.mngr.get(i));
        }
        for (int i = 0; i < allEmployee.othr.size(); i++) {
            emp.add(allEmployee.othr.get(i));
        }

        sort(emp);
        for (Employee o : emp) {
            System.out.println(o);
        }
    }


    public static void sortEmploymentDay(AllEmployee allEmployee){
        ArrayList<Employee> emp = new ArrayList<>();
        for (int i = 0; i < allEmployee.wrkr.size(); i++){
            emp.add(allEmployee.wrkr.get(i));
        }
        for (int i = 0; i < allEmployee.mngr.size(); i++){
            emp.add(allEmployee.mngr.get(i));
        }
        for (int i = 0; i < allEmployee.othr.size(); i++){
            emp.add(allEmployee.othr.get(i));
        }
        emp.sort(employmentDayComparator);
        for (Employee o: emp){
            System.out.println(o);
        }
    }

    public static void convertToXml(AllEmployee jopa) throws IOException, JAXBException {

        JAXBContext context = JAXBContext.newInstance(AllEmployee.class, Worker.class, Other.class, Manager.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jopa, new File("c:/temp/employees.xml"));
    }

    public static AllEmployee convertFromXML() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AllEmployee.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        AllEmployee emps = (AllEmployee) jaxbUnmarshaller.unmarshal( new File("c:/temp/employees.xml") );
        return emps;

    }


    @XmlRootElement
public static class AllEmployee{
        @XmlElement(name = "worker")
        public List<Worker> wrkr = new ArrayList<>();
        @XmlElement(name = "other")
        public List<Other> othr = new ArrayList<>();
        @XmlElement(name = "manager")
        public List<Manager> mngr = new ArrayList<>();

        public AllEmployee(ArrayList<Worker> workers, ArrayList<Other> others, ArrayList<Manager> managers){
            this.wrkr = workers;
            this.othr = others;
            this.mngr = managers;
        }

        public AllEmployee(){

        }
}

}






