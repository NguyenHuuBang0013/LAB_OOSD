package com.quanlykhachsan.forms;

import com.quanlykhachsan.services.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class FrmTraPhong extends JFrame {
    final TraPhongService s=new TraPhongService();final DanhMucService dm=new DanhMucService();
    final JComboBox<Item> dat=new JComboBox<>(),nvDB=new JComboBox<>(),nvHD=new JComboBox<>(),ht=new JComboBox<>();
    final JTable phong=new JTable(),tn=new JTable(),db=new JTable(),hd=new JTable();
    final JTextField soDB=UI.text(),mucDo=UI.text(),soHD=UI.text(),maTT=UI.text(),phongTxt=UI.text(),hdChon=UI.text();
    final JSpinner denBu=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0)),soNgay=new JSpinner(new SpinnerNumberModel(1,1,365,1)),tienTT=new JSpinner(new SpinnerNumberModel(0.0,0.0,1e12,10000.0));
    final java.util.List<Item> dbItems=new ArrayList<>();

    public FrmTraPhong(){
        UI.prepare(this,"Trả phòng - Đền bù - Hóa đơn - Thanh toán",1200,760);UI.moneySpinner(denBu);UI.moneySpinner(tienTT);phongTxt.setEditable(false);hdChon.setEditable(false);
        ht.addItem(new Item("Tiền mặt","Tiền mặt",null,null,0));ht.addItem(new Item("Chuyển khoản","Chuyển khoản",null,null,0));ht.addItem(new Item("Thẻ","Thẻ",null,null,0));ht.addItem(new Item("Ví điện tử","Ví điện tử",null,null,0));
        dat.addActionListener(e->loadRooms());phong.getSelectionModel().addListSelectionListener(e->loadTN());hd.getSelectionModel().addListSelectionListener(e->selectHD());add(build());load();
    }
    JPanel build(){
        JPanel root=UI.whitePanel(),top=UI.formPanel();UI.add(top,UI.label("Phiếu đang ở:"),0,0,1);UI.add(top,dat,0,1,1);UI.add(top,UI.label("Phòng:"),0,2,1);UI.add(top,phongTxt,0,3,1);
        JTabbedPane tabs=new JTabbedPane();UI.styleTabs(tabs);tabs.add("Tiện nghi / Đền bù",damagePanel());tabs.add("Hóa đơn / Thanh toán",invoicePanel());root.add(top,BorderLayout.NORTH);root.add(tabs,BorderLayout.CENTER);return root;
    }
    JPanel damagePanel(){
        JPanel p=UI.whitePanel(),center=new JPanel(new GridLayout(1,2,8,0));center.add(UI.scroll(tn));center.add(UI.scroll(db));
        JPanel f=UI.formPanel();UI.add(f,UI.label("Số phiếu đền bù:"),0,0,1);UI.add(f,soDB,0,1,1);UI.add(f,UI.label("Mức độ:"),0,2,1);UI.add(f,mucDo,0,3,1);UI.add(f,UI.label("Số tiền:"),0,4,1);UI.add(f,denBu,0,5,1);UI.add(f,UI.label("Nhân viên:"),0,6,1);UI.add(f,nvDB,0,7,1);
        JButton add=UI.button("Thêm vào danh sách"),save=UI.button("Lập phiếu đền bù");UI.add(f,add,1,5,1);UI.add(f,save,1,7,1);add.addActionListener(e->addDB());save.addActionListener(e->saveDB());p.add(f,BorderLayout.NORTH);p.add(center,BorderLayout.CENTER);return p;
    }
    JPanel invoicePanel(){
        JPanel p=UI.whitePanel(),f=UI.formPanel();UI.add(f,UI.label("Số hóa đơn:"),0,0,1);UI.add(f,soHD,0,1,1);UI.add(f,UI.label("Số ngày tính tiền:"),0,2,1);UI.add(f,soNgay,0,3,1);UI.add(f,UI.label("NV lập HĐ:"),0,4,1);UI.add(f,nvHD,0,5,1);JButton lap=UI.button("Lập hóa đơn");UI.add(f,lap,0,6,1);lap.addActionListener(e->lapHD());
        UI.add(f,UI.label("Hóa đơn chọn:"),1,0,1);UI.add(f,hdChon,1,1,1);UI.add(f,UI.label("Mã thanh toán:"),1,2,1);UI.add(f,maTT,1,3,1);UI.add(f,UI.label("Hình thức:"),1,4,1);UI.add(f,ht,1,5,1);UI.add(f,UI.label("Số tiền:"),1,6,1);UI.add(f,tienTT,1,7,1);
        JButton pay=UI.button("Thanh toán"),tra=UI.button("Hoàn tất trả phòng");UI.add(f,pay,2,6,1);UI.add(f,tra,2,7,1);pay.addActionListener(e->pay());tra.addActionListener(e->tra());p.add(f,BorderLayout.NORTH);p.add(UI.scroll(hd),BorderLayout.CENTER);return p;
    }
    void load(){try{fill(dat,s.layPhieuDangO(),0,0);fill(nvDB,dm.layNhanVien(),0,1);fill(nvHD,dm.layNhanVien(),0,1);hd.setModel(s.layHoaDon());loadRooms();}catch(Exception e){UI.msg(this,e.getMessage());}}
    void loadRooms(){Item i=(Item)dat.getSelectedItem();if(i!=null)phong.setModel(s.layPhongTheoPhieu(i.id));loadTN();}
    void loadTN(){if(phong.getSelectedRow()<0)return;int r=phong.convertRowIndexToModel(phong.getSelectedRow());phongTxt.setText(phong.getModel().getValueAt(r,0).toString());tn.setModel(s.layTienNghiPhong(phongTxt.getText()));}
    void addDB(){if(tn.getSelectedRow()<0)return;int r=tn.convertRowIndexToModel(tn.getSelectedRow());String id=tn.getModel().getValueAt(r,0).toString(),name=tn.getModel().getValueAt(r,1).toString();for(Item x:dbItems)if(x.id.equals(id)){UI.msg(this,"Tiện nghi đã có trong phiếu đền bù.");return;}dbItems.add(new Item(id,name,null,mucDo.getText().trim(),((Number)denBu.getValue()).doubleValue()));DefaultTableModel m=new DefaultTableModel(new String[]{"Mã tiện nghi","Loại","Mức độ","Số tiền"},0);for(Item x:dbItems)m.addRow(new Object[]{x.id,x.name,x.text,x.amount});db.setModel(m);}
    void saveDB(){Item d=(Item)dat.getSelectedItem(),n=(Item)nvDB.getSelectedItem();if(d==null||n==null)return;Result r=s.lapDenBu(soDB.getText().trim(),d.id,phongTxt.getText(),new Date(),n.id,new ArrayList<>(dbItems));UI.msg(this,r.message());if(r.success()){dbItems.clear();db.setModel(new DefaultTableModel());}}
    void lapHD(){Item d=(Item)dat.getSelectedItem(),n=(Item)nvHD.getSelectedItem();if(d==null||n==null)return;Result r=s.lapHoaDon(soHD.getText().trim(),d.id,new Date(),n.id,(int)((Number)soNgay.getValue()).doubleValue());UI.msg(this,r.message());hd.setModel(s.layHoaDon());}
    void selectHD(){if(hd.getSelectedRow()>=0){int r=hd.convertRowIndexToModel(hd.getSelectedRow());hdChon.setText(hd.getModel().getValueAt(r,0).toString());}}
    void pay(){Item h=(Item)ht.getSelectedItem();Result r=s.thanhToan(maTT.getText().trim(),hdChon.getText().trim(),new Date(),h==null?"":h.id,((Number)tienTT.getValue()).doubleValue());UI.msg(this,r.message());hd.setModel(s.layHoaDon());}
    void tra(){Item d=(Item)dat.getSelectedItem();if(d==null)return;Result r=s.traPhong(d.id,new Date());UI.msg(this,r.message());load();}
    void fill(JComboBox<Item> c,DefaultTableModel m,int id,int name){c.removeAllItems();for(int i=0;i<m.getRowCount();i++)c.addItem(new Item(m.getValueAt(i,id).toString(),m.getValueAt(i,name).toString(),null,null,0));}
}
