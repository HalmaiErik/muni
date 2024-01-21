import { Card, CardActionArea, CardContent, CardMedia, Paper, Stack, Typography } from "@mui/material";
import { AccountDto } from "../../api/dtos";
import styles from "./AccountsList.module.css"
import { useNavigate } from "react-router-dom";

type Props = {
    accounts: AccountDto[];
};

const AccountsList = ({ accounts }: Props) => {
    const navigate = useNavigate();

    const onSelectAccount = (accountId: string) => {
        navigate(`/${accountId}`);
    };

    return (
        <div>
            {accounts.map((account, index) =>
                <div key={index} className={styles.container}>
                    <Card>
                        <CardActionArea className={styles.cardAction} onClick={() => onSelectAccount(account.externalId)}>
                            <CardMedia className={styles.cardMedia} component="img" image={account.institutionLogo} />
                            <CardContent>
                                <Stack>
                                    <Typography variant="button" gutterBottom>
                                        {account.name}
                                    </Typography>
                                    <Typography variant="overline" gutterBottom>
                                        {account.iban}
                                    </Typography>
                                    <Typography variant="overline" gutterBottom>
                                        {account.institutionName}
                                    </Typography>
                                </Stack>
                            </CardContent>
                        </CardActionArea>
                    </Card>
                </div>
            )
            }
        </div >
    );
};

export default AccountsList;