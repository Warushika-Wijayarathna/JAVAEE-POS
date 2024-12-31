package dao.custom.impl;

import dao.custom.ItemDAO;
import entity.Item;

import java.util.List;

public class ItemDAOImpl implements ItemDAO {
    @Override
    public boolean add(Item entity) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Item entity) throws Exception {
        return false;
    }

    @Override
    public boolean update(Item entity) throws Exception {
        return false;
    }

    @Override
    public Object search(Item entity) throws Exception {
        return null;
    }

    @Override
    public List<Item> getAll() throws Exception {
        return null;
    }

    @Override
    public Item exist(String id) throws Exception {
        return null;
    }
}
