package bookStore.model.module;


import bookStore.model.module.common.UserRegistrationAM;

import java.util.List;

import oracle.jbo.Row;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.ViewObjectImpl;

// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 26 14:55:18 IST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class UserRegistrationAMImpl extends ApplicationModuleImpl implements UserRegistrationAM {
    /**
     * This is the default constructor (do not remove).
     */
    public UserRegistrationAMImpl() {
    }


    /**
     * Container's getter for GroupMembers1.
     * @return GroupMembers1
     */
    public ViewObjectImpl getGroupMembers1() {
        return (ViewObjectImpl)findViewObject("GroupMembers1");
    }

    public DBSequence getNextDBSequence() {
        return new DBSequence(new SequenceImpl("BOOK_STORE_SEQ", getDBTransaction()).getSequenceNumber());
    }

    public Integer registerUser(List userAttrNames, List userAttrValues, String groupName) {

        ViewObjectImpl userVO = getUsers1();
        Row newUser = userVO.createRow();

        SequenceImpl seq = new SequenceImpl("BOOK_STORE_SEQ", getDBTransaction());
        Number userId = seq.getSequenceNumber();
        DBSequence dbSeq = new DBSequence(userId);
        userAttrValues.set(3, dbSeq);

        newUser.setAttributeValues(userAttrNames, userAttrValues);

        ViewObjectImpl groupMembersVO = getGroupMembers1();
        Row newGroupMember = groupMembersVO.createRow();
        newGroupMember.setAttribute("GName", groupName);
        newGroupMember.setAttribute("GMember", newUser.getAttribute("UName"));

        userVO.insertRow(newUser);
        groupMembersVO.insertRow(newGroupMember);
        getDBTransaction().commit();
        return userId.intValue();
    }

    /**
     * Container's getter for Users1.
     * @return Users1
     */
    public ViewObjectImpl getUsers1() {
        return (ViewObjectImpl)findViewObject("Users1");
    }
}
