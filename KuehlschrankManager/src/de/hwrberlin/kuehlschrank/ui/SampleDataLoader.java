package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.FridgeManager;

import java.time.LocalDate;

/** Loads demo products into the fridge for the first run. */
public class SampleDataLoader {
    public static void load(FridgeManager manager) {
        manager.addProduct(new FridgeProduct("Milk",           ProductCategory.DAIRY,            0.3,   "litres",  LocalDate.now().plusDays(2),  1.0,   "Door shelf"));
        manager.addProduct(new FridgeProduct("Yoghurt",        ProductCategory.DAIRY,            2.0,   "pieces",  LocalDate.now().plusDays(4),  1.0,   "Top shelf"));
        manager.addProduct(new FridgeProduct("Cheese",         ProductCategory.DAIRY,          150.0,   "grams",   LocalDate.now().plusDays(7),  100.0, "Top shelf"));
        manager.addProduct(new FridgeProduct("Bell Pepper",    ProductCategory.FRUIT_VEGETABLES, 2.0,   "pieces",  LocalDate.now().plusDays(3),  1.0,   "Vegetable drawer"));
        manager.addProduct(new FridgeProduct("Carrots",        ProductCategory.FRUIT_VEGETABLES, 300.0, "grams",   LocalDate.now().plusDays(5),  200.0, "Vegetable drawer"));
        manager.addProduct(new FridgeProduct("Chicken Breast", ProductCategory.MEAT_FISH,        400.0, "grams",   LocalDate.now().plusDays(1),  0.0,   "Bottom shelf"));
        manager.addProduct(new Product("Salami",               ProductCategory.MEAT_FISH,         50.0, "grams",   LocalDate.now().minusDays(1), 0.0));
        manager.addProduct(new Product("Orange Juice",         ProductCategory.BEVERAGES,          0.2, "litres",  LocalDate.now().plusDays(10), 1.0));
        manager.addProduct(new Product("Insulin",              ProductCategory.MEDICATION,         1.0, "bottle",  LocalDate.now().plusDays(60), 1.0));
    }
}
