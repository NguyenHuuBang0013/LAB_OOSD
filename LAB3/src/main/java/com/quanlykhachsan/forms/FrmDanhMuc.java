package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.*;
import javax.swing.*;
import java.awt.*;

public class FrmDanhMuc extends JFrame {
    final DanhMucService s = new DanhMucService();
    final JTable dgvKhu = new JTable(), dgvNV = new JTable(), dgvLoai = new JTable(), dgvDV = new JTable(), dgvQD = new JTable();
    final JTextField khuMa=UI.text(), khuTen=UI.text(), nvMa=UI.text(), nvTen=UI.text(), nvVT=UI.text(), nvSDT=UI.text(),
            loaiMa=UI.text(), loaiTen=UI.text(), dvMa=UI.text(), dvTen=UI.text(), dvDvt=UI.text(), qdMa=UI.text(), qdMuc=UI.text();
    final JSpinner dvGia=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0)), qdTien=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0));
    final JComboBox<Item> qdLoai=new JComboBox<>();

    public FrmDanhMuc() {
        UI.prepare(this, "Danh mục khách sạn", 1000, 680);
        UI.moneySpinner(dvGia); UI.moneySpinner(qdTien);
        JTabbedPane tabs=new JTabbedPane(); UI.styleTabs(tabs);
        tabs.add("Khu vực", tabKhu());
        tabs.add("Nhân viên", tabNV());
        tabs.add("Loại tiện nghi", tabLoai());
        tabs.add("Dịch vụ", tabDV());
        tabs.add("Quy định đền bù", tabQD());
        add(tabs);
        load();
    }

    JPanel tabKhu(){
        JPanel p=UI.whitePanel(), f=UI.formPanel();
        UI.add(f,UI.label("Mã:"),0,0,1); UI.add(f,khuMa,0,1,1);
        UI.add(f,UI.label("Tên:"),0,2,1); UI.add(f,khuTen,0,3,1);
        JButton b=UI.button("Thêm"); UI.add(f,b,0,4,1);
        b.addActionListener(e->doResult(s.themKhu(khuMa.getText().trim(),khuTen.getText().trim())));
        p.add(f,BorderLayout.NORTH); p.add(UI.scroll(dgvKhu),BorderLayout.CENTER); return p;
    }
    JPanel tabNV(){
        JPanel p=UI.whitePanel(), f=UI.formPanel(); String[] lab={"Mã","Họ tên","Vai trò","SĐT"}; JTextField[] x={nvMa,nvTen,nvVT,nvSDT};
        for(int i=0;i<4;i++){UI.add(f,UI.label(lab[i]+":"),0,i*2,1);UI.add(f,x[i],0,i*2+1,1);}
        JButton b=UI.button("Thêm"); UI.add(f,b,1,7,1);
        b.addActionListener(e->doResult(s.themNhanVien(nvMa.getText().trim(),nvTen.getText().trim(),nvVT.getText().trim(),nvSDT.getText().trim())));
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(dgvNV),BorderLayout.CENTER);return p;
    }
    JPanel tabLoai(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Mã:"),0,0,1);UI.add(f,loaiMa,0,1,1);UI.add(f,UI.label("Tên:"),0,2,1);UI.add(f,loaiTen,0,3,1);
        JButton b=UI.button("Thêm");UI.add(f,b,0,4,1);b.addActionListener(e->doResult(s.themLoai(loaiMa.getText().trim(),loaiTen.getText().trim())));
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(dgvLoai),BorderLayout.CENTER);return p;
    }
    JPanel tabDV(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Mã:"),0,0,1);UI.add(f,dvMa,0,1,1);UI.add(f,UI.label("Tên:"),0,2,1);UI.add(f,dvTen,0,3,1);
        UI.add(f,UI.label("Đơn vị:"),1,0,1);UI.add(f,dvDvt,1,1,1);UI.add(f,UI.label("Đơn giá:"),1,2,1);UI.add(f,dvGia,1,3,1);
        JButton b=UI.button("Thêm");UI.add(f,b,1,4,1);b.addActionListener(e->doResult(s.themDichVu(dvMa.getText().trim(),dvTen.getText().trim(),dvDvt.getText().trim(),((Number)dvGia.getValue()).doubleValue())));
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(dgvDV),BorderLayout.CENTER);return p;
    }
    JPanel tabQD(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();
        UI.add(f,UI.label("Mã:"),0,0,1);UI.add(f,qdMa,0,1,1);UI.add(f,UI.label("Loại tiện nghi:"),0,2,1);UI.add(f,qdLoai,0,3,1);
        UI.add(f,UI.label("Mức độ:"),1,0,1);UI.add(f,qdMuc,1,1,1);UI.add(f,UI.label("Mức đền bù:"),1,2,1);UI.add(f,qdTien,1,3,1);
        JButton b=UI.button("Thêm");UI.add(f,b,1,4,1);b.addActionListener(e->{Item i=(Item)qdLoai.getSelectedItem();doResult(s.themQuyDinh(qdMa.getText().trim(),i==null?"":i.id,qdMuc.getText().trim(),((Number)qdTien.getValue()).doubleValue()));});
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(dgvQD),BorderLayout.CENTER);return p;
    }
    void load(){try{dgvKhu.setModel(s.layKhuVuc());dgvNV.setModel(s.layNhanVien());dgvLoai.setModel(s.layLoaiTienNghi());dgvDV.setModel(s.layDichVu());dgvQD.setModel(s.layQuyDinh());qdLoai.removeAllItems();var m=s.layLoaiTienNghi();for(int i=0;i<m.getRowCount();i++)qdLoai.addItem(new Item(m.getValueAt(i,0).toString(),m.getValueAt(i,1).toString(),null,null,0));}catch(Exception e){UI.msg(this,e.getMessage());}}
    void doResult(Result r){UI.msg(this,r.message());if(r.success())load();}
}
