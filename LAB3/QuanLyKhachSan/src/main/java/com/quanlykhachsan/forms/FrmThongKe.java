package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.ThongKeService;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class FrmThongKe extends JFrame {
    final ThongKeService s=new ThongKeService();
    final JTable tong=new JTable(),dv=new JTable();
    final JSpinner tu=new JSpinner(new SpinnerDateModel()),den=new JSpinner(new SpinnerDateModel());
    public FrmThongKe(){
        UI.prepare(this,"Thống kê khách sạn",1000,600);UI.dateSpinner(tu);UI.dateSpinner(den);
        JPanel root=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Từ ngày:"),0,0,1);UI.add(f,tu,0,1,1);
        UI.add(f,UI.label("Đến ngày:"),0,2,1);UI.add(f,den,0,3,1);
        JButton b=UI.button("Thống kê");UI.add(f,b,0,4,1);b.addActionListener(e->run());
        JTabbedPane tabs=new JTabbedPane();UI.styleTabs(tabs);tabs.add("Tổng hợp",UI.scroll(tong));tabs.add("Dịch vụ sử dụng",UI.scroll(dv));
        root.add(f,BorderLayout.NORTH);root.add(tabs,BorderLayout.CENTER);add(root);
    }
    void run(){Date a=(Date)tu.getValue(),b=(Date)den.getValue();if(b.before(a)){UI.msg(this,"Đến ngày không được trước từ ngày.");return;}try{tong.setModel(s.tongHop(a,b));dv.setModel(s.dichVu(a,b));}catch(Exception e){UI.msg(this,e.getMessage());}}
}
