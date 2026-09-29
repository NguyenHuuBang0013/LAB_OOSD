package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class FrmDatPhong extends JFrame {
    final DatPhongService s=new DatPhongService();final DanhMucService dm=new DanhMucService();
    final JTable khach=new JTable(),phong=new JTable(),chonTable=new JTable(),phieu=new JTable(),ct=new JTable(),nguoi=new JTable();
    final JTextField maKH=UI.text(),tenKH=UI.text(),cmnd=UI.text(),qt=UI.text(),sdt=UI.text(),soPhieu=UI.text(),phieuChon=UI.text(),nguoiPhong=UI.text(),nguoiTen=UI.text(),nguoiCMND=UI.text(),nguoiQT=UI.text();
    final JComboBox<Item> cboKhach=new JComboBox<>(),cboNV=new JComboBox<>(),cboKenh=new JComboBox<>();
    final JSpinner coc=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0)),soNguoi=new JSpinner(new SpinnerNumberModel(1,1,1000,1));
    final JSpinner lap=new JSpinner(new SpinnerDateModel()),nhan=new JSpinner(new SpinnerDateModel()),tra=new JSpinner(new SpinnerDateModel());
    final java.util.List<Item> selected=new ArrayList<>();

    public FrmDatPhong(){
        UI.prepare(this,"Khách hàng - Đặt phòng - Nhận phòng",1200,760);UI.moneySpinner(coc);UI.dateSpinner(lap);UI.dateSpinner(nhan);UI.dateSpinner(tra);
        JTabbedPane t=new JTabbedPane();UI.styleTabs(t);t.add("Khách hàng",tabKH());t.add("Đặt phòng",tabDat());t.add("Nhận phòng / Người lưu trú",tabNhan());add(t);
        cboKenh.addItem(new Item("Điện thoại","Điện thoại",null,null,0));cboKenh.addItem(new Item("Website","Website",null,null,0));cboKenh.addItem(new Item("Trực tiếp","Trực tiếp",null,null,0));
        phieu.getSelectionModel().addListSelectionListener(e->loadSelected());load();
    }
    JPanel tabKH(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();String[] l={"Mã khách","Họ tên","CMND/CCCD","Quốc tịch","SĐT"};JTextField[] x={maKH,tenKH,cmnd,qt,sdt};
        for(int i=0;i<5;i++){UI.add(f,UI.label(l[i]+":"),0,i*2,1);UI.add(f,x[i],0,i*2+1,1);}JButton b=UI.button("Thêm khách");UI.add(f,b,1,9,1);
        b.addActionListener(e->doResult(s.themKhach(maKH.getText().trim(),tenKH.getText().trim(),cmnd.getText().trim(),qt.getText().trim(),sdt.getText().trim())));
        p.add(f,BorderLayout.NORTH);p.add(UI.scroll(khach),BorderLayout.CENTER);return p;
    }
    JPanel tabDat(){
        JPanel p=UI.whitePanel(),top=UI.formPanel();
        UI.add(top,UI.label("Số phiếu đặt:"),0,0,1);UI.add(top,soPhieu,0,1,1);
        UI.add(top,UI.label("Khách:"),0,2,1);UI.add(top,cboKhach,0,3,1);
        UI.add(top,UI.label("Kênh đặt:"),0,4,1);UI.add(top,cboKenh,0,5,1);
        UI.add(top,UI.label("Tiền cọc:"),0,6,1);UI.add(top,coc,0,7,1);
        UI.add(top,UI.label("Ngày lập:"),1,0,1);UI.add(top,lap,1,1,1);
        UI.add(top,UI.label("Ngày nhận:"),1,2,1);UI.add(top,nhan,1,3,1);
        UI.add(top,UI.label("Ngày trả dự kiến:"),1,4,1);UI.add(top,tra,1,5,1);
        UI.add(top,UI.label("Lễ tân:"),1,6,1);UI.add(top,cboNV,1,7,1);

        JPanel center=new JPanel(new GridLayout(1,2,8,0));center.setBorder(BorderFactory.createEmptyBorder(5,8,5,8));center.add(UI.scroll(phong));center.add(UI.scroll(chonTable));
        JPanel bottom=UI.formPanel();JButton add=UI.button("Thêm phòng"),remove=UI.button("Bỏ phòng"),save=UI.button("Lập phiếu đặt");
        UI.add(bottom,UI.label("Số người phòng chọn:"),0,0,1);UI.add(bottom,soNguoi,0,1,1);UI.add(bottom,add,0,2,1);UI.add(bottom,remove,0,3,1);UI.add(bottom,save,0,4,1);
        add.addActionListener(e->addRoom());remove.addActionListener(e->removeRoom());
        save.addActionListener(e->{Item k=(Item)cboKhach.getSelectedItem(),n=(Item)cboNV.getSelectedItem(),ch=(Item)cboKenh.getSelectedItem();doResult(s.taoDatPhong(soPhieu.getText().trim(),k==null?"":k.id,n==null?"":n.id,(Date)lap.getValue(),(Date)nhan.getValue(),(Date)tra.getValue(),((Number)coc.getValue()).doubleValue(),ch==null?"":ch.id,new ArrayList<>(selected)));});
        p.add(top,BorderLayout.NORTH);p.add(center,BorderLayout.CENTER);p.add(bottom,BorderLayout.SOUTH);return p;
    }
    JPanel tabNhan(){
        JPanel p=UI.whitePanel(),top=UI.formPanel();
        UI.add(top,UI.label("Phiếu đặt chọn:"),0,0,1);UI.add(top,phieuChon,0,1,1);JButton nh=UI.button("Nhận phòng"),no=UI.button("No-show");UI.add(top,nh,0,2,1);UI.add(top,no,0,3,1);
        JPanel info=UI.formPanel();UI.add(info,UI.label("Phòng:"),0,0,1);UI.add(info,nguoiPhong,0,1,1);UI.add(info,UI.label("Họ tên:"),0,2,1);UI.add(info,nguoiTen,0,3,1);UI.add(info,UI.label("CMND:"),0,4,1);UI.add(info,nguoiCMND,0,5,1);UI.add(info,UI.label("Quốc tịch:"),0,6,1);UI.add(info,nguoiQT,0,7,1);JButton add=UI.button("Thêm người lưu trú");UI.add(info,add,1,7,1);
        nh.addActionListener(e->doResult(s.nhanPhong(phieuChon.getText().trim(),new Date())));no.addActionListener(e->doResult(s.noShow(phieuChon.getText().trim())));add.addActionListener(e->doResult(s.themNguoi(phieuChon.getText().trim(),nguoiPhong.getText().trim(),nguoiTen.getText().trim(),nguoiCMND.getText().trim(),nguoiQT.getText().trim())));
        JPanel center=new JPanel(new GridLayout(1,2,8,0));center.add(UI.scroll(ct));center.add(UI.scroll(nguoi));
        p.add(top,BorderLayout.NORTH);p.add(info,BorderLayout.SOUTH);p.add(center,BorderLayout.CENTER);return p;
    }
    void addRoom(){if(phong.getSelectedRow()<0)return;int row=phong.convertRowIndexToModel(phong.getSelectedRow());String id=phong.getModel().getValueAt(row,0).toString();for(Item x:selected)if(x.id.equals(id)){UI.msg(this,"Phòng đã có trong phiếu.");return;}selected.add(new Item(id,id,null,null,((Number)soNguoi.getValue()).doubleValue()));refreshChosen();}
    void removeRoom(){if(chonTable.getSelectedRow()<0)return;int row=chonTable.convertRowIndexToModel(chonTable.getSelectedRow());if(row>=0&&row<selected.size())selected.remove(row);refreshChosen();}
    void refreshChosen(){DefaultTableModel m=new DefaultTableModel(new String[]{"Phòng chọn","Số người","Đơn giá/ngày"},0);for(Item x:selected)m.addRow(new Object[]{x.id,(int)x.amount,""});chonTable.setModel(m);}
    void load(){try{khach.setModel(s.layKhach());phong.setModel(s.layPhong());phieu.setModel(s.layPhieuDat());fill(cboKhach,s.layKhach(),0,1);fill(cboNV,dm.layNhanVien(),0,1);}catch(Exception e){UI.msg(this,e.getMessage());}}
    void loadSelected(){int r=phieu.getSelectedRow();if(r<0)return;r=phieu.convertRowIndexToModel(r);String so=phieu.getModel().getValueAt(r,0).toString();phieuChon.setText(so);ct.setModel(s.layChiTiet(so));nguoi.setModel(s.layNguoi(so));}
    void fill(JComboBox<Item> c,DefaultTableModel m,int id,int name){c.removeAllItems();for(int i=0;i<m.getRowCount();i++)c.addItem(new Item(m.getValueAt(i,id).toString(),m.getValueAt(i,name).toString(),null,null,0));}
    void doResult(Result r){UI.msg(this,r.message());if(r.success()){load();loadSelected();}}
}
