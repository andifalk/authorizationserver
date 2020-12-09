package com.example.authorizationserver.scim.api.resource.mapper;

import com.example.authorizationserver.scim.api.resource.*;
import com.example.authorizationserver.scim.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CreateScimUserResourceMapper {

    public CreateScimUserResource mapEntityToResource(ScimUserEntity scimUserEntity) {
        return new CreateScimUserResource(
                new ScimMetaResource("User", null, null,
                        "0", null),
                scimUserEntity.getIdentifier(), scimUserEntity.getExternalId(), scimUserEntity.getUserName(),
                scimUserEntity.getFamilyName(), scimUserEntity.getGivenName(), scimUserEntity.getMiddleName(),
                scimUserEntity.getHonorificPrefix(), scimUserEntity.getHonorificSuffix(), scimUserEntity.getNickName(),
                scimUserEntity.getProfileUrl(), scimUserEntity.getTitle(), scimUserEntity.getUserType(),
                scimUserEntity.getPreferredLanguage(), scimUserEntity.getLocale(), scimUserEntity.getTimezone(),
                scimUserEntity.isActive(), scimUserEntity.getPassword(),
                scimUserEntity.getEmails() != null ? scimUserEntity.getEmails().stream().map(e -> new ScimEmailResource(e.getEmail(), e.getType(), e.isPrimaryEmail())).collect(Collectors.toSet()) : null,
                scimUserEntity.getPhoneNumbers() != null ? scimUserEntity.getPhoneNumbers().stream().map(p -> new ScimPhoneNumberResource(p.getPhone(), p.getType())).collect(Collectors.toSet()) : null,
                scimUserEntity.getIms() != null ? scimUserEntity.getIms().stream().map(i -> new ScimImsResource(i.getIms(), i.getType())).collect(Collectors.toSet()) : null,
                scimUserEntity.getPhotos() != null ? scimUserEntity.getPhotos().stream().map(p -> new ScimPhotoResource(p.getPhotoUrl(), p.getType())).collect(Collectors.toSet()) : null,
                scimUserEntity.getAddresses() != null ? scimUserEntity.getAddresses().stream().map(a -> new ScimAddressResource(a.getStreetAddress(), a.getLocality(), a.getRegion(), a.getPostalCode(), a.getCountry(), a.getType(), a.isPrimaryAddress())).collect(Collectors.toSet()) : null,
                scimUserEntity.getGroups() != null ? scimUserEntity.getGroups().stream().map(g ->
                    new ScimRefResource(g.getGroup().getIdentifier(), null, g.getGroup().getDisplayName())
                ).collect(Collectors.toSet()) : null,
                scimUserEntity.getEntitlements(), scimUserEntity.getRoles(), scimUserEntity.getX509Certificates());
    }

    public ScimUserEntity mapResourceToEntity(CreateScimUserResource createScimUserResource) {

        return new ScimUserEntity(createScimUserResource.getIdentifier(),
                createScimUserResource.getExternalId(), createScimUserResource.getUserName(), createScimUserResource.getFamilyName(),
                createScimUserResource.getGivenName(), createScimUserResource.getMiddleName(), createScimUserResource.getHonorificPrefix(), createScimUserResource.getHonorificSuffix(),
                createScimUserResource.getNickName(), createScimUserResource.getProfileUrl(), createScimUserResource.getTitle(), createScimUserResource.getUserType(),
                createScimUserResource.getPreferredLanguage(), createScimUserResource.getLocale(), createScimUserResource.getTimezone(), createScimUserResource.isActive(),
                createScimUserResource.getPassword(),
                createScimUserResource.getEmails() != null ? createScimUserResource.getEmails().stream().map(e -> new ScimEmailEntity(e.getValue(), e.getType(), e.isPrimary())).collect(Collectors.toSet()) : null,
                createScimUserResource.getPhoneNumbers() != null ? createScimUserResource.getPhoneNumbers().stream().map(p -> new ScimPhoneNumberEntity(p.getValue(), p.getType())).collect(Collectors.toSet()) : null,
                createScimUserResource.getIms() != null ? createScimUserResource.getIms().stream().map(p -> new ScimImsEntity(p.getValue(), p.getType())).collect(Collectors.toSet()) : null,
                createScimUserResource.getPhotos() != null ? createScimUserResource.getPhotos().stream().map(p -> new ScimPhotoEntity(p.getValue(), p.getType())).collect(Collectors.toSet()) : null,
                createScimUserResource.getAddresses() != null ? createScimUserResource.getAddresses().stream().map(a -> new ScimAddressEntity(a.getStreetAddress(), a.getLocality(), a.getRegion(), a.getPostalCode(), a.getCountry(), a.getType(), a.isPrimary())).collect(Collectors.toSet()) : null,
                createScimUserResource.getGroups() != null ? createScimUserResource.getGroups().stream().map(p ->
                        new ScimUserGroupEntity(new ScimUserEntity(), new ScimGroupEntity(p.getValue(), null, p.getDisplay(), null))).collect(Collectors.toSet()) : null,
                        createScimUserResource.getEntitlements(), createScimUserResource.getRoles(), createScimUserResource.getX509Certificates()
        );
    }
}
