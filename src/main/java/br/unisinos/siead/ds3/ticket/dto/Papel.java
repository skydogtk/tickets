package br.unisinos.siead.ds3.ticket.dto;

public class Papel {

    private int id;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Papel{" + "id=" + id + ", desc=" + desc + '}';
    }
}
