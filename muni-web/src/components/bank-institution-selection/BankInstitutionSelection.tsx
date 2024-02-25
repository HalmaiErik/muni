import CloseIcon from '@mui/icons-material/Close';
import { Avatar, IconButton, List, ListItemAvatar, ListItemButton, ListItemText, Typography } from "@mui/material";
import { useCountryInstitutions, useCreateRequisition } from "../../api/bank-account-data-api";
import { InstitutionDto } from "../../api/dtos";
import { LOCAL_STORAGE_INSTITUTION_LOGO, LOCAL_STORAGE_INSTITUTION_NAME, WEB_HOME_URL } from "../../utils/constants";

type Props = {
    onClose?: () => void;
};

const BankInstitutionSelection = ({ onClose }: Props) => {
    const { data: institutions } = useCountryInstitutions('RO');
    const { mutate: createRequisition } = useCreateRequisition();

    const onSelectInstitution = (institution: InstitutionDto) => {
        createRequisition({ institutionId: institution.id, redirectUrl: WEB_HOME_URL });
        window.localStorage.setItem(LOCAL_STORAGE_INSTITUTION_NAME, institution.name);
        window.localStorage.setItem(LOCAL_STORAGE_INSTITUTION_LOGO, institution.logo);
    }

    const isLastElement = (ind: number) => {
        return institutions && ind === institutions.length - 1;
    }

    return institutions ?
        <>
            <div style={{ display: 'flex' }}>
                <Typography sx={{ flexGrow: 1 }} variant="h4" component="div" textAlign={'center'} marginBottom={2}>Select a bank institution</Typography >
                {onClose && (
                    <IconButton size="large" onClick={onClose}>
                        <CloseIcon fontSize="inherit" />
                    </IconButton>
                )}
            </div>

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
        :
        null
};

export default BankInstitutionSelection;