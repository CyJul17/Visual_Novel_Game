package com.hiraeth;
import javax.swing.SwingUtilities;

import com.hiraeth.Managers.SceneManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SceneManager::new);
    }
}
