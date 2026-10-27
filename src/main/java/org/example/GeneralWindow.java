package org.example;

import org.example.immitator.FakeSerialPortAdapter;
import org.example.immitator.SerialPortAdapter;
import org.example.panels.general_panel.GeneralPanel;
import org.example.panels.general_panel.GeneralPanelController;
import org.example.panels.generated_panel.GeneratedPanel;
import org.example.panels.synchronization_panel.SynchronisationPanel;
import org.example.panels.synchronization_panel.SynchronisationPanelController;
import org.example.panels.observer.ServiceStation;

import javax.swing.*;

public class GeneralWindow extends JFrame {
    public GeneralWindow(){
        ServiceStation serviceStation=new ServiceStation();
        GeneralPanel generalPanel=new GeneralPanel();
       // SerialPortAdapter fakeAdapter = new FakeSerialPortAdapter("FAKE_COM1", 1); // или 2
       // PortService portService = new PortService(fakeAdapter);
        PortService portService=new PortService();
        GeneralPanelController generalPanelController=new GeneralPanelController(generalPanel,serviceStation,portService);
        SynchronisationPanel synchronisationPanel =new SynchronisationPanel();
        SynchronisationPanelController synchronisationPanelController =new SynchronisationPanelController(synchronisationPanel,
                portService,serviceStation);
        GeneratedPanel generatedPanel=new GeneratedPanel();
        serviceStation.addObserver(synchronisationPanel);
        serviceStation.addObserver(generatedPanel);
        JTabbedPane tabbedPane=new JTabbedPane();
        tabbedPane.addTab("Основное окно",generalPanel);
        tabbedPane.addTab("Панель синхронизатора", synchronisationPanel);
        tabbedPane.addTab("Панель генератора",generatedPanel);
        this.add(tabbedPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
    }

}
