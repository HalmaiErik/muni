import { Avatar, List, ListItemButton, ListItemAvatar, ListItemText, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { createRequisition, getCountryInstitutions } from "../../api/bank-account-data-api";
import { InstitutionDto } from "../../api/dtos";
import { LOCAL_STORAGE_INSTITUTION_LOGO, LOCAL_STORAGE_INSTITUTION_NAME, WEB_HOME_URL, WEB_SAVE_CUSTOMER_URL } from "../../utils/constants";

const BankInstitutionSelection = () => {
    const [institutions, setInstitutions] = useState<InstitutionDto[]>([]);
    useEffect(() => {
        getCountryInstitutions('RO').then((data) => {
            setInstitutions(data);
        })
    }, []);

    const onSelectInstitution = (institution: InstitutionDto) => {
        createRequisition({ institutionId: institution.id, redirectUrl: WEB_HOME_URL })
            .then(req => {
                window.localStorage.setItem(LOCAL_STORAGE_INSTITUTION_NAME, institution.name);
                window.localStorage.setItem(LOCAL_STORAGE_INSTITUTION_LOGO, institution.logo);
                window.location.href = req.link;
            });
    }

    const isLastElement = (ind: number) => {
        return ind === institutions.length - 1;
    }

    return (institutions && (
        <>
            <Typography variant="h6" component="div" textAlign={'center'} marginBottom={2}>Select a bank institution</Typography >
            <List>
                {institutions.map((institution, ind) => (
                    <ListItemButton key={ind} divider={!isLastElement(ind)} onClick={() => onSelectInstitution(institution)}>
                        <ListItemAvatar>
                            <Avatar src={institution.logo} />
                        </ListItemAvatar>
                        <ListItemText primary={institution.name} />
                    </ListItemButton>
                ))}
            </List>
        </>
    ))

};

export default BankInstitutionSelection;