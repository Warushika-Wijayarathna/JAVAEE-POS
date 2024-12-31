package dao.custom.impl;

import dao.custom.CustomerDAO;
import entity.Customer;

import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public boolean add(Customer entity) throws Exception {
        return false;
    }

    @Override
    public boolean delete(Customer entity) throws Exception {
        return false;
    }

    @Override
    public boolean update(Customer entity) throws Exception {
        return false;
    }

    @Override
    public Object search(Customer entity) throws Exception {
        return null;
    }

    @Override
    public List<Customer> getAll() throws Exception {
        return null;
    }

    @Override
    public Customer exist(String id) throws Exception {
        return null;
    }
}
