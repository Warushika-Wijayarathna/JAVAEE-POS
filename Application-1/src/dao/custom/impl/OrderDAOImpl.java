package dao.custom.impl;

import dao.custom.OrderDAO;
import entity.Order;

import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public boolean add(Order entity) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Order entity) throws Exception {
        return false;
    }

    @Override
    public boolean update(Order entity) throws Exception {
        return false;
    }

    @Override
    public Object search(Order entity) throws Exception {
        return null;
    }

    @Override
    public List<Order> getAll() throws Exception {
        return null;
    }

    @Override
    public Order exist(String id) throws Exception {
        return null;
    }
}
