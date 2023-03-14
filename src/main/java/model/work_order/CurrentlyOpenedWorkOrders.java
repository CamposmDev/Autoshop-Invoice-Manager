package model.work_order;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @brief Keeps track of currently opened work orders in tabs by storing their ids in a list
 */
public class CurrentlyOpenedWorkOrders {
    private static final String DAT_FILE = "./currowos.dat";
    private LinkedList<Integer> ids;

    public CurrentlyOpenedWorkOrders() {
        /* TODO: Implement save feature */
        try {
            File file = new File(DAT_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                ids = (LinkedList<Integer>) ois.readObject();
                ois.close();
                System.out.println("Loaded " + DAT_FILE);
            } else {
                System.out.println("Missing " + DAT_FILE);
                ids = new LinkedList<>();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            File file = new File(DAT_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ids);
            oos.close();
            System.out.println("Saved " + DAT_FILE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void add(int workOrderId) {
        if (ids.contains(workOrderId)) {
            ids.removeFirstOccurrence(workOrderId);
        }
        ids.addLast(workOrderId);
        System.out.println("currOWOs: " + ids);
    }

    public void remove(int workOrderId) {
        ids.removeFirstOccurrence(workOrderId);
        System.out.println("currOWOs: " + ids);
    }

    public Iterator<Integer> iterator() {
        return ids.iterator();
    }
}
