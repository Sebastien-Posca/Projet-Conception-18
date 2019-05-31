package fr.unice.polytech.cookieFactory.Shop;

import fr.unice.polytech.cookieFactory.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SupplierCatalog {

    private List<Supplier> supplierList;

    public SupplierCatalog() {
        supplierList = new ArrayList<>();
    }

    public Supplier addSupplier(Supplier supplier) {
        String supplierName = supplier.getName().trim().toLowerCase();

        for (Supplier storedSupplier : supplierList) {
            String currentSupplierName = storedSupplier.getName().trim().toLowerCase();

            if (supplierName.equals(currentSupplierName)) {
                return storedSupplier;
            }
        }

        supplierList.add(supplier);

        return supplier;
    }

    public Supplier getSupplier(int supplierId) {
        List<Supplier> foundSuppliers = supplierList.stream()
                .filter(s -> s.getSupplierId() == supplierId)
                .collect(Collectors.toList());
        if (foundSuppliers.isEmpty())
            return null;
        return foundSuppliers.get(0);
    }
}
