package com.iflytek.kv.key;

import com.iflytek.kv.base.BaseDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ContactDimension extends BaseDimension {

    private String name;
    private String phone;


    public ContactDimension() {
    }

    @Override
    public int compareTo(BaseDimension o) {
        ContactDimension other = (ContactDimension) o;
        return this.phone.compareTo(other.phone);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(name);
        dataOutput.writeUTF(phone);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.name = dataInput.readUTF();
        this.phone = dataInput.readUTF();
    }

    @Override
    public String toString() {
        return name + "\t" + phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
