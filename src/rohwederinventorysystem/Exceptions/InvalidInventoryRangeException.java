/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rohwederinventorysystem.Exceptions;

/**
 *
 * @author tim
 */
public class InvalidInventoryRangeException extends Exception {
    public InvalidInventoryRangeException(String message) {
        super(message);
    }
}