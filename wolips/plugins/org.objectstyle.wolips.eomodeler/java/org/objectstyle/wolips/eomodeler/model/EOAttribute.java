/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.kvc.IKey;
import org.objectstyle.wolips.eomodeler.kvc.ResolvedKey;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EOAttribute extends AbstractEOArgument implements IEOAttribute, ISortableEOModelObject {
  public static final String PRIMARY_KEY = "primaryKey";
  public static final String CLASS_PROPERTY = "classProperty";
  public static final String USED_FOR_LOCKING = "usedForLocking";
  public static final String PROTOTYPE = "prototype";
  public static final String READ_FORMAT = "readFormat";
  public static final String WRITE_FORMAT = "writeFormat";
  public static final String CLIENT_CLASS_PROPERTY = "clientClassProperty";
  public static final String INDEXED = "indexed";
  public static final String READ_ONLY = "readOnly";

  private static final String[] PROTOTYPED_PROPERTIES = { EOAttribute.COLUMN_NAME, EOAttribute.ALLOWS_NULL, EOAttribute.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, EOAttribute.EXTERNAL_TYPE, EOAttribute.FACTORY_METHOD_ARGUMENT_TYPE, EOAttribute.PRECISION, EOAttribute.SCALE, EOAttribute.VALUE_CLASS_NAME, EOAttribute.VALUE_FACTORY_METHOD_NAME, EOAttribute.VALUE_TYPE, EOAttribute.DEFINITION, EOAttribute.WIDTH, EOAttribute.READ_FORMAT, EOAttribute.WRITE_FORMAT, EOAttribute.INDEXED, EOAttribute.READ_ONLY };

  private static Map myCachedPropertyKeys;

  static {
    myCachedPropertyKeys = new HashMap();
  }

  protected static synchronized IKey getPropertyKey(String _property) {
    IKey key = (IKey) myCachedPropertyKeys.get(_property);
    if (key == null) {
      key = new ResolvedKey(EOAttribute.class, _property);
      myCachedPropertyKeys.put(_property, key);
    }
    return key;
  }

  private EOEntity myEntity;
  private String myPrototypeName;
  private EOAttribute myCachedPrototype;
  private Boolean myClassProperty;
  private Boolean myPrimaryKey;
  private Boolean myUsedForLocking;
  private Boolean myClientClassProperty;
  private Boolean myIndexed;
  private Boolean myReadOnly;
  private String myReadFormat;
  private String myWriteFormat;

  public EOAttribute() {
    // DO NOTHING
  }

  public EOAttribute(String _name) {
    super(_name);
  }

  public EOAttribute(String _name, String _definition) {
    super(_name, _definition);
  }

  public void pasted() {
    // DO NOTHING
  }

  protected AbstractEOArgument _createArgument(String _name) {
    return new EOAttribute(_name);
  }

  public EOAttribute cloneAttribute() {
    EOAttribute attribute = (EOAttribute) _cloneArgument();
    attribute.myPrototypeName = myPrototypeName;
    attribute.myCachedPrototype = myCachedPrototype;
    attribute.myClassProperty = myClassProperty;
    attribute.myPrimaryKey = myPrimaryKey;
    attribute.myUsedForLocking = myUsedForLocking;
    attribute.myClientClassProperty = myClientClassProperty;
    attribute.myIndexed = myIndexed;
    attribute.myReadOnly = myReadOnly;
    attribute.myReadFormat = myReadFormat;
    attribute.myWriteFormat = myWriteFormat;
    return attribute;
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (myEntity != null) {
      myEntity._attributeChanged(this, _propertyName, _oldValue, _newValue);
    }
  }

  public int hashCode() {
    return ((myEntity == null) ? 1 : myEntity.hashCode()) * super.hashCode();
  }

  public boolean equals(Object _obj) {
    boolean equals = false;
    if (_obj instanceof EOAttribute) {
      EOAttribute attribute = (EOAttribute) _obj;
      equals = (attribute == this) || (ComparisonUtils.equals(attribute.myEntity, myEntity) && ComparisonUtils.equals(attribute.getName(), getName()));
    }
    return equals;
  }

  public Boolean isToMany() {
    return Boolean.FALSE;
  }

  public boolean isPrototyped(String _property) {
    boolean prototyped = false;
    if (myPrototypeName != null) {
      EOAttribute prototype = getPrototype();
      if (prototype != null) {
        IKey key = EOAttribute.getPropertyKey(_property);
        Object value = key.getValue(this);
        if (value != null) {
          Object prototypeValue = key.getValue(prototype);
          prototyped = value.equals(prototypeValue);
        }
      }
    }
    return prototyped;
  }

  public boolean isPrototyped() {
    boolean prototyped = false;
    EOAttribute prototype = getPrototype();
    if (prototype != null) {
      prototyped = true;
    }
    return prototyped;
  }

  public boolean isFlattened() {
    return getDefinition() != null;
  }

  public boolean isInherited() {
    boolean inherited = false;
    if (myEntity != null) {
      EOEntity parent = myEntity.getParent();
      if (parent != null) {
        EOAttribute attribute = parent.getAttributeNamed(getName());
        inherited = (attribute != null);
      }
    }
    return inherited;
  }

  public EOAttribute getPrototype() {
    if (myCachedPrototype == null && myPrototypeName != null && myEntity != null) {
      myCachedPrototype = myEntity.getModel().getPrototypeAttributeNamed(myPrototypeName);
    }
    return myCachedPrototype;
  }

  public void clearCachedPrototype(Set _failures, boolean _reload) {
    if (myPrototypeName != null) {
      clearCachedPrototype(myPrototypeName, _failures, true, _reload);
    }
  }

  public void clearCachedPrototype(String _prototypeName, Set _failures, boolean _callSetPrototype, boolean _reload) {
    myCachedPrototype = null;
    myPrototypeName = _prototypeName;
    if (_reload && _prototypeName != null && myEntity != null) {
      EOAttribute prototype = myEntity.getModel().getPrototypeAttributeNamed(_prototypeName);
      if (_callSetPrototype) {
        setPrototype(prototype);
      }
      if (prototype == null) {
        myCachedPrototype = prototype;
        myPrototypeName = _prototypeName;
        if (_failures != null) {
          _failures.add(new MissingPrototypeFailure(_prototypeName, this));
        }
      }
    }
  }

  public void setPrototype(EOAttribute _prototype) {
    setPrototype(_prototype, true);
  }

  public void setPrototype(EOAttribute _prototype, boolean _updateFromPrototype) {
    EOAttribute oldPrototype = getPrototype();
    boolean prototypeNameChanged = true;
    if (_prototype == null && oldPrototype == null) {
      prototypeNameChanged = false;
    }
    else if (ComparisonUtils.equals(_prototype, oldPrototype)) {
      prototypeNameChanged = false;
    }

    EODataType oldDataType = getDataType();
    Map oldValues = new HashMap();
    for (int propertyNum = 0; propertyNum < PROTOTYPED_PROPERTIES.length; propertyNum++) {
      String propertyName = PROTOTYPED_PROPERTIES[propertyNum];
      Object oldValue = EOAttribute.getPropertyKey(propertyName).getValue(this);
      oldValues.put(propertyName, oldValue);
    }
    myCachedPrototype = _prototype;
    if (_prototype == null) {
      myPrototypeName = null;
    }
    else {
      myPrototypeName = _prototype.getName();
    }
    firePropertyChange(EOAttribute.PROTOTYPE, oldPrototype, _prototype);
    if (prototypeNameChanged && _updateFromPrototype) {
      for (int propertyNum = 0; propertyNum < PROTOTYPED_PROPERTIES.length; propertyNum++) {
        String propertyName = PROTOTYPED_PROPERTIES[propertyNum];
        IKey propertyKey = EOAttribute.getPropertyKey(propertyName);
        Object newValue = propertyKey.getValue(this);
        Object oldValue = oldValues.get(propertyName);
        propertyKey.setValue(this, newValue);
        firePropertyChange(propertyName, oldValue, newValue);
      }
      updateDataType(oldDataType);
    }
  }

  protected Object _prototypeValueIfNull(String _property, Object _value) {
    Object value = _value;
    if ((value == null || (value instanceof String && ((String) _value).length() == 0)) && myPrototypeName != null) {
      EOAttribute prototype = getPrototype();
      if (prototype != null) {
        value = EOAttribute.getPropertyKey(_property).getValue(prototype);
      }
    }
    return value;
  }

  protected Object _nullIfPrototyped(String _property, Object _value) {
    Object value = _value;
    if (value != null && myPrototypeName != null) {
      EOAttribute prototype = getPrototype();
      if (prototype != null && value.equals(EOAttribute.getPropertyKey(_property).getValue(prototype))) {
        value = null;
      }
    }
    return value;
  }

  public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
    String name = (String) _prototypeValueIfNull(AbstractEOArgument.NAME, _name);
    if (name == null) {
      throw new NullPointerException(Messages.getString("EOAttribute.noBlankAttributeNames"));
    }
    if (myEntity != null) {
      myEntity._checkForDuplicateAttributeName(this, name, null);
    }
    super.setName((String) _nullIfPrototyped(AbstractEOArgument.NAME, name), _fireEvents);
  }

  public String getName() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.NAME, super.getName());
  }

  public Boolean getReadOnly() {
    return isReadOnly();
  }

  public Boolean isReadOnly() {
    return (Boolean) _prototypeValueIfNull(EOAttribute.READ_ONLY, myReadOnly);
  }

  public void setReadOnly(Boolean _readOnly) {
    setReadOnly(_readOnly, true);
  }

  public void setReadOnly(Boolean _readOnly, boolean _fireEvents) {
    Boolean oldReadOnly = getAllowsNull();
    myReadOnly = (Boolean) _nullIfPrototyped(EOAttribute.READ_ONLY, _readOnly);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.READ_ONLY, oldReadOnly, getReadOnly());
    }
  }

  public Boolean getIndexed() {
    return isIndexed();
  }

  public Boolean isIndexed() {
    return (Boolean) _prototypeValueIfNull(EOAttribute.INDEXED, myIndexed);
  }

  public void setIndexed(Boolean _indexed) {
    setIndexed(_indexed, true);
  }

  public void setIndexed(Boolean _indexed, boolean _fireEvents) {
    Boolean oldIndexed = getIndexed();
    myIndexed = (Boolean) _nullIfPrototyped(EOAttribute.INDEXED, _indexed);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.INDEXED, oldIndexed, getIndexed());
    }
  }

  public Boolean isAllowsNull() {
    return (Boolean) _prototypeValueIfNull(AbstractEOArgument.ALLOWS_NULL, super.isAllowsNull());
  }

  public void setAllowsNull(Boolean _allowsNull, boolean _fireEvents) {
    Boolean newAllowsNull = _allowsNull;
    if (_fireEvents && BooleanUtils.isTrue(getPrimaryKey())) {
      newAllowsNull = Boolean.FALSE;
    }
    super.setAllowsNull((Boolean) _nullIfPrototyped(AbstractEOArgument.ALLOWS_NULL, newAllowsNull), _fireEvents);
  }

  public Boolean getClassProperty() {
    return isClassProperty();
  }

  public Boolean isClassProperty() {
    return myClassProperty;//(Boolean) _prototypeValueIfNull(EOAttribute.CLASS_PROPERTY, myClassProperty);
  }

  public void setClassProperty(Boolean _classProperty) {
    setClassProperty(_classProperty, true);
  }

  public void setClassProperty(Boolean _classProperty, boolean _fireEvents) {
    Boolean oldClassProperty = getClassProperty();
    //myClassProperty = (Boolean) _nullIfPrototyped(EOAttribute.CLASS_PROPERTY, _classProperty);
    myClassProperty = _classProperty;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.CLASS_PROPERTY, oldClassProperty, getClassProperty());
    }
  }

  public String getColumnName() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.COLUMN_NAME, super.getColumnName());
  }

  public void setColumnName(String _columnName) {
    super.setColumnName((String) _nullIfPrototyped(AbstractEOArgument.COLUMN_NAME, _columnName));
  }

  public void _setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public Boolean getPrimaryKey() {
    return isPrimaryKey();
  }

  public Boolean isPrimaryKey() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.PRIMARY_KEY, myPrimaryKey);
    return myPrimaryKey;
  }

  public void setPrimaryKey(Boolean _primaryKey) {
    setPrimaryKey(_primaryKey, true);
  }

  public void setPrimaryKey(Boolean _primaryKey, boolean _fireEvents) {
    Boolean oldPrimaryKey = getPrimaryKey();
    //myPrimaryKey = (Boolean) _nullIfPrototyped(EOAttribute.PRIMARY_KEY, _primaryKey);
    myPrimaryKey = _primaryKey;
    if (_fireEvents && BooleanUtils.isTrue(_primaryKey)) {
      setAllowsNull(Boolean.FALSE, _fireEvents);
    }
    if (_fireEvents) {
      firePropertyChange(EOAttribute.PRIMARY_KEY, oldPrimaryKey, getPrimaryKey());
    }
  }

  public Boolean getUsedForLocking() {
    return isUsedForLocking();
  }

  public Boolean isUsedForLocking() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.USED_FOR_LOCKING, myUsedForLocking);
    return myUsedForLocking;
  }

  public void setUsedForLocking(Boolean _usedForLocking) {
    setUsedForLocking(_usedForLocking, true);
  }

  public void setUsedForLocking(Boolean _usedForLocking, boolean _fireEvents) {
    Boolean oldUsedForLocking = getUsedForLocking();
    //myUsedForLocking = (Boolean) _nullIfPrototyped(EOAttribute.USED_FOR_LOCKING, _usedForLocking);
    myUsedForLocking = _usedForLocking;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.USED_FOR_LOCKING, oldUsedForLocking, getUsedForLocking());
    }
  }

  public String getAdaptorValueConversionMethodName() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, super.getAdaptorValueConversionMethodName());
  }

  public void setAdaptorValueConversionMethodName(String _adaptorValueConversionMethodName) {
    super.setAdaptorValueConversionMethodName((String) _nullIfPrototyped(AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, _adaptorValueConversionMethodName));
  }

  public String getExternalType() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.EXTERNAL_TYPE, super.getExternalType());
  }

  public void setExternalType(String _externalType) {
    super.setExternalType((String) _nullIfPrototyped(AbstractEOArgument.EXTERNAL_TYPE, _externalType));
  }

  public EOFactoryMethodArgumentType getFactoryMethodArgumentType() {
    return (EOFactoryMethodArgumentType) _prototypeValueIfNull(AbstractEOArgument.FACTORY_METHOD_ARGUMENT_TYPE, super.getFactoryMethodArgumentType());
  }

  public void setFactoryMethodArgumentType(EOFactoryMethodArgumentType _factoryMethodArgumentType) {
    super.setFactoryMethodArgumentType((EOFactoryMethodArgumentType) _nullIfPrototyped(AbstractEOArgument.FACTORY_METHOD_ARGUMENT_TYPE, _factoryMethodArgumentType));
  }

  public Integer getPrecision() {
    return (Integer) _prototypeValueIfNull(AbstractEOArgument.PRECISION, super.getPrecision());
  }

  public void setPrecision(Integer _precision) {
    super.setPrecision((Integer) _nullIfPrototyped(AbstractEOArgument.PRECISION, _precision));
  }

  public Integer getScale() {
    return (Integer) _prototypeValueIfNull(AbstractEOArgument.SCALE, super.getScale());
  }

  public void setScale(Integer _scale) {
    super.setScale((Integer) _nullIfPrototyped(AbstractEOArgument.SCALE, _scale));
  }

  public String getServerTimeZone() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.SERVER_TIME_ZONE, super.getServerTimeZone());
  }

  public void setServerTimeZone(String _serverTimeZone) {
    super.setServerTimeZone((String) _nullIfPrototyped(AbstractEOArgument.SERVER_TIME_ZONE, _serverTimeZone));
  }

  public String getValueClassName() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.VALUE_CLASS_NAME, super.getValueClassName());
  }

  public synchronized void setValueClassName(String _valueClassName, boolean _updateDataType) {
    super.setValueClassName((String) _nullIfPrototyped(AbstractEOArgument.VALUE_CLASS_NAME, _valueClassName), _updateDataType);
  }

  public String getValueFactoryMethodName() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.VALUE_FACTORY_METHOD_NAME, super.getValueFactoryMethodName());
  }

  public void setValueFactoryMethodName(String _valueFactoryMethodName) {
    super.setValueFactoryMethodName((String) _nullIfPrototyped(AbstractEOArgument.VALUE_FACTORY_METHOD_NAME, _valueFactoryMethodName));
  }

  public String getValueType() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.VALUE_TYPE, super.getValueType());
  }

  public synchronized void setValueType(String _valueType, boolean _updateDataType) {
    super.setValueType((String) _nullIfPrototyped(AbstractEOArgument.VALUE_TYPE, _valueType), _updateDataType);
  }

  public Integer getWidth() {
    return (Integer) _prototypeValueIfNull(AbstractEOArgument.WIDTH, super.getWidth());
  }

  public void setWidth(Integer _width) {
    super.setWidth((Integer) _nullIfPrototyped(AbstractEOArgument.WIDTH, _width));
  }

  public String getDefinition() {
    return (String) _prototypeValueIfNull(AbstractEOArgument.DEFINITION, super.getDefinition());
  }

  public void setDefinition(String _definition) {
    super.setDefinition((String) _nullIfPrototyped(AbstractEOArgument.DEFINITION, _definition));
  }

  public String getReadFormat() {
    return (String) _prototypeValueIfNull(EOAttribute.READ_FORMAT, myReadFormat);
  }

  public void setReadFormat(String _readFormat) {
    String oldReadFormat = getReadFormat();
    myReadFormat = (String) _nullIfPrototyped(EOAttribute.READ_FORMAT, _readFormat);
    firePropertyChange(EOAttribute.READ_FORMAT, oldReadFormat, getReadFormat());
  }

  public String getWriteFormat() {
    return (String) _prototypeValueIfNull(EOAttribute.WRITE_FORMAT, myWriteFormat);
  }

  public void setWriteFormat(String _writeFormat) {
    String oldWriteFormat = getWriteFormat();
    myWriteFormat = (String) _nullIfPrototyped(EOAttribute.WRITE_FORMAT, _writeFormat);
    firePropertyChange(EOAttribute.WRITE_FORMAT, oldWriteFormat, getWriteFormat());
  }

  public void setClientClassProperty(Boolean _clientClassProperty) {
    setClientClassProperty(_clientClassProperty, false);
  }

  public void setClientClassProperty(Boolean _clientClassProperty, boolean _fireEvents) {
    Boolean oldClientClassProperty = getClientClassProperty();
    //myClientClassProperty = (Boolean) _nullIfPrototyped(EOAttribute.CLIENT_CLASS_PROPERTY, _clientClassProperty);
    myClientClassProperty = _clientClassProperty;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.CLIENT_CLASS_PROPERTY, oldClientClassProperty, getClientClassProperty());
    }
  }

  public Boolean getClientClassProperty() {
    return isClientClassProperty();
  }

  public Boolean isClientClassProperty() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.CLIENT_CLASS_PROPERTY, myClientClassProperty);
    return myClientClassProperty;
  }

  public Set getReferenceFailures() {
    Set referenceFailures = new HashSet();
    Iterator referencingRelationshipsIter = getReferencingRelationships(true).iterator();
    while (referencingRelationshipsIter.hasNext()) {
      EORelationship referencingRelationship = (EORelationship) referencingRelationshipsIter.next();
      referenceFailures.add(new EOAttributeRelationshipReferenceFailure(this, referencingRelationship));
    }
    return referenceFailures;
  }

  public List getReferencingRelationships(boolean _includeInheritedAttributes) {
    List referencingRelationships = new LinkedList();
    if (myEntity != null) {
      Iterator modelsIter = getEntity().getModel().getModelGroup().getModels().iterator();
      while (modelsIter.hasNext()) {
        EOModel model = (EOModel) modelsIter.next();
        Iterator entitiesIter = model.getEntities().iterator();
        while (entitiesIter.hasNext()) {
          EOEntity entity = (EOEntity) entitiesIter.next();
          Iterator relationshipsIter = entity.getRelationships().iterator();
          while (relationshipsIter.hasNext()) {
            EORelationship relationship = (EORelationship) relationshipsIter.next();
            if (relationship.isRelatedTo(this)) {
              referencingRelationships.add(relationship);
            }
          }
        }
      }

      if (_includeInheritedAttributes && myEntity != null) {
        String name = getName();
        Iterator childrenEntitiesIter = myEntity.getChildrenEntities().iterator();
        while (childrenEntitiesIter.hasNext()) {
          EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
          EOAttribute childAttribute = childEntity.getAttributeNamed(name);
          if (childAttribute != null) {
            referencingRelationships.addAll(childAttribute.getReferencingRelationships(_includeInheritedAttributes));
          }
        }
      }
    }
    return referencingRelationships;
  }

  public void loadFromMap(EOModelMap _attributeMap, Set _failures) {
    super.loadFromMap(_attributeMap, _failures);
    myReadOnly = _attributeMap.getBoolean("isReadOnly");
    myIndexed = _attributeMap.getBoolean("isIndexed");
    if (_attributeMap.containsKey("selectFormat")) {
      myReadFormat = _attributeMap.getString("selectFormat", true);
    }
    else {
      myReadFormat = _attributeMap.getString("readFormat", true);
    }
    if (_attributeMap.containsKey("updateFormat")) {
      myWriteFormat = _attributeMap.getString("updateFormat", true);
    }
    else if (_attributeMap.containsKey("insertFormat")) {
      myWriteFormat = _attributeMap.getString("insertFormat", true);
    }
    else {
      myWriteFormat = _attributeMap.getString("writeFormat", true);
    }
  }

  public EOModelMap toMap() {
    EOModelMap attributeMap = super.toMap();
    if (myPrototypeName != null) {
      attributeMap.setString("prototypeName", myPrototypeName, true);
    }
    attributeMap.setBoolean("isReadOnly", myReadOnly, EOModelMap.YN);
    attributeMap.setBoolean("isIndexed", myIndexed, EOModelMap.YN);
    attributeMap.setString("readFormat", myReadFormat, true);
    attributeMap.remove("selectFormat");
    attributeMap.setString("writeFormat", myWriteFormat, true);
    attributeMap.remove("updateFormat");
    attributeMap.remove("insertFormat");
    return attributeMap;
  }

  public void resolve(Set _failures) {
    String prototypeName = getArgumentMap().getString("prototypeName", true);
    clearCachedPrototype(prototypeName, _failures, false, true);
  }

  public void verify(Set _failures) {
    String name = getName();
    if (name == null || name.trim().length() == 0) {
      _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + " has an empty name."));
    }
    else {
      if (name.indexOf(' ') != -1) {
        _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + "'s name has a space in it."));
      }
      if (!StringUtils.isLowercaseFirstLetter(name)) {
        _failures.add(new EOModelVerificationFailure("Attribute names should not be capitalized, but " + getFullyQualifiedName() + " is."));
      }
    }
    if (!myEntity.isPrototype()) {
      if (!isFlattened()) {
        String columnName = getColumnName();
        if (columnName == null || columnName.trim().length() == 0) {
          _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + " does not have a column name set."));
        }
        else if (columnName.indexOf(' ') != -1) {
          _failures.add(new EOModelVerificationFailure(getFullyQualifiedName() + "'s column name '" + columnName + "' has a space in it."));
        }
      }
    }
  }

  public String getFullyQualifiedName() {
    return ((myEntity == null) ? "?" : myEntity.getFullyQualifiedName()) + "/Attribute:" + getName();
  }

  public String toString() {
    return "[EOAttribute: " + getName() + "]";
  }
}
