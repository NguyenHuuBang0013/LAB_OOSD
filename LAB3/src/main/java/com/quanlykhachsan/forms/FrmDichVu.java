package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.*;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class FrmDichVu extends JFrame {
    final DichVuService s=new DichVuService(); final DanhMucService dm=new DanhMucService();
    final JTable lichSu=new JTable();
    final JComboBox<Item> luot=new JComboBox<>(),dv=new JComboBox<>(),nv=new JComboBox<>();
    final JTextField phong=UI.text();
    final JSpinner ngay=new JSpinner(new SpinnerDateModel()),sl=new JSpinner(new SpinnerNumberModel(1,1,100000,1));

    public FrmDichVu(){
        UI.prepare(this,"Sử dụng dịch vụ",1000,600);UI.dateSpinner(ngay);phong.setEditable(false);
        JPanel root=UI.whitePanel();
        JPanel f=UI.formPanel();
        UI.add(f,UI.label("Phiếu lưu trú:"),0,0,1);UI.add(f,luot,0,1,1);
        UI.add(f,UI.label("Phòng:"),0,2,1);UI.add(f,phong,0,3,1);
        UI.add(f,UI.label("Dịch vụ:"),0,4,1);UI.add(f,dv,0,5,1);
        UI.add(f,UI.label("Ngày sử dụng:"),1,0,1);UI.add(f,ngay,1,1,1);
        UI.add(f,UI.label("Số lượng:"),1,2,1);UI.add(f,sl,1,3,1);
        UI.add(f,UI.label("Nhân viên:"),1,4,1);UI.add(f,nv,1,5,1);
        JButton b=UI.button("Ghi nhận");UI.add(f,b,1,6,1);b.addActionListener(e->ghi());
        root.add(f,BorderLayout.NORTH);root.add(UI.scroll(lichSu),BorderLayout.CENTER);add(root);load();
    }
    void load(){try{var m=s.layPhieuDangO();fill(luot,m,0,0);fill(dv,s.layDichVu(),0,1);fill(nv,dm.layNhanVien(),0,1);selected();}catch(Exception e){UI.msg(this,e.getMessage());}}
    void selected(){Object o=luot.getSelectedItem();if(o instanceof Item i){var m=s.layPhieuDangO();for(int r=0;r<m.getRowCount();r++)if(m.getValueAt(r,0).toString().equals(i.id)){phong.setText(m.getValueAt(r,2).toString());break;}lichSu.setModel(s.layLichSu(i.id));}}
    void ghi(){Item l=(Item)luot.getSelectedItem(),d=(Item)dv.getSelectedItem(),n=(Item)nv.getSelectedItem();if(l==null||d==null||n==null)return;Result r=s.ghiNhan(l.id,phong.getText(),(Date)ngay.getValue(),n.id,d.id,(int)((Number)sl.getValue()).doubleValue());UI.msg(this,r.message());if(r.success())selected();}
    void fill(JComboBox<Item> c,DefaultTableModel m,int id,int name){c.removeAllItems();for(int i=0;i<m.getRowCount();i++)c.addItem(new Item(m.getValueAt(i,id).toString(),m.getValueAt(i,name).toString(),null,null,0));}
}
