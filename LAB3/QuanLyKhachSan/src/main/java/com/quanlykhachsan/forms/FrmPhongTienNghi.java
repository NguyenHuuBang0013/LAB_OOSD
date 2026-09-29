package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.*;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class FrmPhongTienNghi extends JFrame {
    final PhongTienNghiService s=new PhongTienNghiService(); final DanhMucService dm=new DanhMucService();
    final JTable phong=new JTable(),tn=new JTable(),ld=new JTable();
    final JTextField txtPhong=UI.text(),txtMaTN=UI.text(),txtTT=UI.text(),txtSoLD=UI.text(),txtTTLD=UI.text(),txtGhiChu=UI.text();
    final JComboBox<Item> cboKhu=new JComboBox<>(),cboLoai=new JComboBox<>(),cboTN=new JComboBox<>(),cboPhong=new JComboBox<>(),cboNV=new JComboBox<>();
    final JSpinner max=new JSpinner(new SpinnerNumberModel(1,1,1000,1)),gia=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0)),stt=new JSpinner(new SpinnerNumberModel(1,1,1000,1));
    final JSpinner date=new JSpinner(new SpinnerDateModel());

    public FrmPhongTienNghi(){
        UI.prepare(this,"Phòng - Tiện nghi - Phiếu lắp đặt",1100,700);
        UI.moneySpinner(gia);UI.dateSpinner(date);
        JTabbedPane t=new JTabbedPane();UI.styleTabs(t);
        t.add("Phòng",tabPhong());t.add("Tiện nghi",tabTN());t.add("Lắp đặt / luân chuyển",tabLD());add(t);load();
    }
    JPanel tabPhong(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Số phòng:"),0,0,1);UI.add(f,txtPhong,0,1,1);
        UI.add(f,UI.label("Khu vực:"),0,2,1);UI.add(f,cboKhu,0,3,1);
        UI.add(f,UI.label("Số người tối đa:"),0,4,1);UI.add(f,max,0,5,1);
        UI.add(f,UI.label("Đơn giá/ngày:"),0,6,1);UI.add(f,gia,0,7,1);
        JButton b=UI.button("Thêm phòng");UI.add(f,b,1,7,1);
        b.addActionListener(e->{Item i=(Item)cboKhu.getSelectedItem();doResult(s.themPhong(txtPhong.getText().trim(),i==null?"":i.id,(int)((Number)max.getValue()).doubleValue(),((Number)gia.getValue()).doubleValue()));});
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(phong),BorderLayout.CENTER);return p;
    }
    JPanel tabTN(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Mã tiện nghi:"),0,0,1);UI.add(f,txtMaTN,0,1,1);
        UI.add(f,UI.label("Loại tiện nghi:"),0,2,1);UI.add(f,cboLoai,0,3,1);
        UI.add(f,UI.label("Số thứ tự:"),0,4,1);UI.add(f,stt,0,5,1);
        UI.add(f,UI.label("Tình trạng:"),0,6,1);UI.add(f,txtTT,0,7,1);
        JButton b=UI.button("Thêm tiện nghi");UI.add(f,b,1,7,1);
        b.addActionListener(e->{Item i=(Item)cboLoai.getSelectedItem();doResult(s.themTienNghi(txtMaTN.getText().trim(),i==null?"":i.id,(int)((Number)stt.getValue()).doubleValue(),txtTT.getText().trim()));});
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(tn),BorderLayout.CENTER);return p;
    }
    JPanel tabLD(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Phiếu lắp đặt:"),0,0,1);UI.add(f,txtSoLD,0,1,1);
        UI.add(f,UI.label("Tiện nghi:"),0,2,1);UI.add(f,cboTN,0,3,1);
        UI.add(f,UI.label("Phòng:"),0,4,1);UI.add(f,cboPhong,0,5,1);
        UI.add(f,UI.label("Ngày:"),1,0,1);UI.add(f,date,1,1,1);
        UI.add(f,UI.label("Tình trạng:"),1,2,1);UI.add(f,txtTTLD,1,3,1);
        UI.add(f,UI.label("Nhân viên:"),1,4,1);UI.add(f,cboNV,1,5,1);
        UI.add(f,UI.label("Ghi chú:"),2,0,1);UI.add(f,txtGhiChu,2,1,5);
        JButton b=UI.button("Lập phiếu");UI.add(f,b,2,7,1);
        b.addActionListener(e->{Item a=(Item)cboTN.getSelectedItem(),q=(Item)cboPhong.getSelectedItem(),n=(Item)cboNV.getSelectedItem();doResult(s.lapDat(txtSoLD.getText().trim(),a==null?"":a.id,q==null?"":q.id,(Date)date.getValue(),txtTTLD.getText().trim(),n==null?"":n.id,txtGhiChu.getText().trim()));});
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(ld),BorderLayout.CENTER);return p;
    }
    void load(){try{phong.setModel(s.layPhong());tn.setModel(s.layTienNghi());ld.setModel(s.layLapDat());fill(cboKhu,dm.layKhuVuc(),0,1);fill(cboLoai,dm.layLoaiTienNghi(),0,1);fill(cboTN,s.layTienNghi(),0,0);fill(cboPhong,s.layPhong(),0,0);fill(cboNV,dm.layNhanVien(),0,1);}catch(Exception e){UI.msg(this,e.getMessage());}}
    void fill(JComboBox<Item> c,javax.swing.table.DefaultTableModel m,int idCol,int nameCol){c.removeAllItems();for(int i=0;i<m.getRowCount();i++)c.addItem(new Item(m.getValueAt(i,idCol).toString(),m.getValueAt(i,nameCol).toString(),null,null,0));}
    void doResult(Result r){UI.msg(this,r.message());if(r.success())load();}
}
